package testproject.biddingservice;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;
import testproject.biddingservice.jpa.Project;
import testproject.biddingservice.jpa.ProjectRepository;

/*
 * About the Project
 * 
 * Technology Choices:
 * First I chose tomcat for the servlet container and mysql for the database because
 * I'm familiar with both of those and they are easy to set up.
 * 
 * I chose to use Spring RequestMapping pattern to map the restful services that I
 * created for this project. The framework allows me to quickly and easily define my
 * web service urls. I used the Spring RequestBody/ResponseBody pattern to interpret
 * the json objects as simple HashMaps. This was done mainly for simplicity's sake.
 * It would have been more memory efficient to convert the json data to java objects,
 * but that would have required additional annotations & configuration. Lastly I used
 * the Spring JPARepository framework to handle interactions with the database. I like
 * this framework because it makes it very simple to convert database records to plain
 * java objects without writing a lot of tedious code.
 * 
 * Architecture Choices:
 * For the most part this application is a straightforward restful service. It has two
 * services that insert records (using POST) and one service that retrieves records
 * (using GET). The url patterns follow typical REST conventions. For error handling
 * I just sent back a json object with an error message explaining the problem to the
 * user. For the GET method, I send back a 404 error if the user tries to retrieve a
 * project that doesn't exist.
 * 
 * This project only has a few pieces of business logic, most of which reside in the
 * controller to handle submitting bids. Whenever a bid is submitted the system makes
 * a few checks and takes some actions. First, it checks if it is passed the project's
 * close date. If so, then the bid isn't accepted. Next, it checks if the bid is
 * above the project's maximum budget (if the project has a maximum budget). If so,
 * then the bid isn't accepted. Finally, it checks if the bid is below the current
 * lowest bid. If it is, then the project's "lowest bid" field is updated to reflect
 * the current bid. This simplifies the process of retrieving the lowest bid on a
 * project. When we get the project, we don't have to compare bids because the
 * database table has a pointer to the current lowest bid.
 * 
 * The last bit of business logic resides in the service to get information about a
 * project. It checks if it is passed the project's close date. If it is, then we
 * return the name and email of the lowest bidder so that the person who posted the
 * project can contact them. At that point, that person is the "winning" bidder.
 * 
 */

@Controller
public class ProjectController
{

    @Autowired
    private ProjectRepository            projectRepo;

    public static final SimpleDateFormat dateFormat  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static final NumberFormat     moneyFormat = NumberFormat.getCurrencyInstance();

    /*
     * Test Cases:
     * 1) Submit a project with a project name, contact email, and close date later than 
     * today.  Leave the other fields blank.  Verify that a new project is created with 
     * the correct name, contact email, and close date. Verify that the other fields are 
     * left blank. Verify that the project's id is returned by this service.
     * 
     * 2) Same as 1, but also send a description, requirements url, and max budget.  
     * Verify that a new projet is created with all values.  Verify that the projet's id
     * is returned by this service.
     * 
     * 3) Same as 1, but the close date should be prior to the current date/time. Verify 
     * that the new project is not created.  Verify that an error message is returned to
     * the client, letting them know that the close date was invalid.
     * 
     * 4) Same as 1, but the close date should be improperly formatted. Verify that the 
     * new project is not created.  Verify that an error message is returned to the 
     * client, letting them know that the close date was invalid.
     * 
     * 5) Same as 1, but leave one of the three fields blank. Verify that the project is
     * not created. Verify that an error message is returned to the client, letting them 
     * know which required field is missing.
     * 
     */
    @RequestMapping(value = "/project", method = RequestMethod.POST)
    @ResponseBody
    public HashMap submitProject(@RequestBody HashMap newProjectRequest)
    {
        HashMap response = new HashMap();
        boolean success = true;

        String projectName = null;
        String description = null;
        String contactEmail = null;
        String requirementsUrl = null;
        BigDecimal maxBudget = null;
        Timestamp closeDate = null;

        try
        {
            projectName = (String)newProjectRequest.get("project_name");
            description = (String)newProjectRequest.get("description");
            contactEmail = (String)newProjectRequest.get("contact_email");
            requirementsUrl = (String)newProjectRequest.get("requirements_url");
            String maxBudgetString = (String)newProjectRequest.get("max_budget");
            maxBudget = maxBudgetString != null ? new BigDecimal(maxBudgetString) : null;
            String closeDateString = (String)newProjectRequest.get("close_date");
            closeDate = new Timestamp(dateFormat.parse(closeDateString).getTime());

            // Check if the close date is on or after the current date
            Date now = new Date();
            if (now.compareTo(closeDate) >= 0)
            {
                success = false;

                response.put("status", "error");
                addMessage(response, "The close date is on or before the current date: " + closeDate);
            }
        }
        catch (ParseException pe)
        {
            success = false;

            response.put("status", "error");
            addMessage(response, "Failed to parse close date: " + newProjectRequest.get("close_date"));
            addMessage(response, pe.getMessage());
        }
        catch (Exception e)
        {
            success = false;

            response.put("status", "error");
            addMessage(response, e.getMessage());
        }

        if (projectName == null || contactEmail == null || closeDate == null || projectName.length() == 0 || contactEmail.length() == 0)
        {
            success = false;

            response.put("status", "error");
            if (projectName == null || projectName.length() == 0)
            {
                addMessage(response, "Project name required");
            }
            else if (contactEmail == null || contactEmail.length() == 0)
            {
                addMessage(response, "Contact email required");
            }
            else if (closeDate == null)
            {
                addMessage(response, "Close date required");
            }
        }
        else
        {
            if (success)
            {
                Project newProject = new Project();
                newProject.setProjectName(projectName);
                newProject.setDescription(description);
                newProject.setContactEmail(contactEmail);
                newProject.setRequirementsUrl(requirementsUrl);
                newProject.setMaxBudget(maxBudget);
                newProject.setCloseDate(closeDate);
                newProject.setLowestBid(null);

                projectRepo.saveAndFlush(newProject);

                response.put("status", "success");
                response.put("project_id", newProject.getId().toString());
            }
        }

        return response;
    }

    /*
     * Test Cases:
     * 1) Retrieve a project with an id, name, contact email, and close date.  Verify that those fields are all rendered 
     * correctly. Verify that the other fields contain an empty string, N/A, or None.
     * 
     * 2) Retrieve a project with a value in each field.  Verify that all of the values are rendered correctly.
     * 
     * 3) Retrieve a project with at least one valid bid after the close date has been reached. Verify that in addition 
     * to the other fields, the system also sends back contact information about the winning bidder.
     * 
     * 4) Retrieve a project with no bids after the close date has been reached.  Verify that in addition to the other 
     * field, th system also sends back a "winning_bidder" field with a value of "None"
     * 
     * 5) Try to retrieve a project that doesn't exist. Verify that you get a 404 error.
     * 
     */
    @RequestMapping(value = "/project/{id}", method = RequestMethod.GET)
    @ResponseBody
    public HashMap getProject(@PathVariable("id") BigInteger id) throws NoSuchRequestHandlingMethodException
    {
        HashMap response = new HashMap();

        Project project = projectRepo.findOne(id);

        if (project != null)
        {
            // These fields are guaranteed to be not null
            response.put("project_id", project.getId());
            response.put("project_name", project.getProjectName());
            response.put("contact_email", project.getContactEmail());
            response.put("close_date", dateFormat.format(project.getCloseDate()));

            // These fields might be null
            response.put("description", project.getDescription() != null ? project.getDescription() : "");
            response.put("requirements_url", project.getRequirementsUrl() != null ? project.getRequirementsUrl() : "");
            response.put("max_budget", project.getMaxBudget() != null ? moneyFormat.format(project.getMaxBudget().doubleValue()) : "N/A");
            response.put("lowest_bid", project.getLowestBid() != null ? moneyFormat.format(project.getLowestBid().getBidAmount().doubleValue()) : "None");

            // Check if the project is finished
            Date now = new Date();
            if (project.getCloseDate().compareTo(now) < 0)
            { // If so, return the winning bidder (if there is one)
                response.put(
                    "winning_bidder",
                    project.getLowestBid() != null ? project.getLowestBid().getName() != null ? project.getLowestBid().getName() + ", " + project.getLowestBid().getContactEmail() : project.getLowestBid().getContactEmail() : "None");
            }
        }
        else
        {
            // Throw a 404 error
            throw new NoSuchRequestHandlingMethodException("getProject", ProjectController.class);
        }

        return response;
    }

    public static void addMessage(HashMap response, String message)
    {
        List messages = (List)response.get("messages");
        if (messages == null)
        {
            messages = new ArrayList();
        }

        messages.add(message);
        response.put("messages", messages);
    }

}
