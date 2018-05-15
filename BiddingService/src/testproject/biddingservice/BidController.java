package testproject.biddingservice;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import testproject.biddingservice.jpa.Bid;
import testproject.biddingservice.jpa.BidRepository;
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
public class BidController
{

    @Autowired
    private ProjectRepository projectRepo;

    @Autowired
    private BidRepository     bidRepo;

    /*
     * Test Cases:
     * 1) Submit a bid with a bid amount and contact email on a project with no lowest bid, no maximum 
     * budget before the proejct's close date. Verify that the bid is submitted successfully and that this 
     * bid is the new lowest bid on the project.
     * 
     * 2) Same as 1, but this time add a name to the bid.  Verify that the bid is submitted successfully and 
     * that this bid is the new lowest bid on the project.
     * 
     * 3) Same as 1, but bid on a project with a lowest bid and bid under that amount. Verify that the bid is
     * submitted successfully and that this bid is the new lowest bid on the project.
     * 
     * 4) Same as 1, but bid on a project with a maximum budget.  Make sure that the bid amount is less than 
     * the maximum budget. Verify that the bid is submitted successfully and that this bid is the new lowest 
     * bid on the project.
     * 
     * 5) Same as 3, but this time bid an amount above the lowest bid amount.  Verify that the bid is 
     * submitted successfully, but the lowest bid on the project stays the same.
     * 
     * 6) Same as 4, but this time the bid is higher than the maximum budget for the project. Verify that the 
     * bid is not saved and the client sees an error message explaining that their bid was over the maximum 
     * budget.
     * 
     * 7) Same as 1, but this time bid on a project after the close date. Verify that the bid is not saved and 
     * the client sees an error message explaining that the project is closed.
     * 
     * 8) Same as 1, but this time bid on a project that doesn't exist. Verify that the bid is not saved and 
     * the client sees an error message explaining that the project could not be found.
     * 
     * 9)Same as 1, but leave the bid amount or contact email field blank. Verify that the bid is not saved and 
     * that the client sees an error message explaining that the field cannot be blank.
     * 
     */
    @RequestMapping(value = "/project/{id}/bid", method = RequestMethod.POST)
    @ResponseBody
    public HashMap submitBid(@PathVariable("id") BigInteger id, @RequestBody HashMap bidRequest)
    {
        HashMap response = new HashMap();
        boolean success = true;

        try
        {
            Project project = projectRepo.findOne(id);

            // Check if the project exists
            if (project != null)
            {
                // Check if the project is still open for bidding
                Timestamp closeDate = project.getCloseDate();
                Date now = new Date();
                if (now.compareTo(closeDate) <= 0)
                {
                    String name = null;
                    String bidAmountString = null;
                    BigDecimal bidAmount = null;
                    String contactEmail = null;

                    name = (String)bidRequest.get("name");
                    bidAmountString = (String)bidRequest.get("bid_amount");

                    try
                    {
                        bidAmount = new BigDecimal(bidAmountString);
                    }
                    catch (NumberFormatException nfe)
                    {
                        success = false;

                        response.put("status", "error");

                        ProjectController.addMessage(response, "The bid amount isn't formatted correctly: " + bidAmountString);
                    }

                    contactEmail = (String)bidRequest.get("contact_email");

                    if (bidAmount == null)
                    {
                        success = false;

                        response.put("status", "error");

                        ProjectController.addMessage(response, "The bid amount cannot be null");
                    }

                    if (contactEmail == null || contactEmail.length() == 0)
                    {
                        success = false;

                        response.put("status", "error");

                        ProjectController.addMessage(response, "The contact email cannot be null");
                    }

                    if (success)
                    {
                        // Check if this bid amount is less than the maximum budget
                        BigDecimal maxBudget = project.getMaxBudget();
                        if (maxBudget == null || maxBudget.compareTo(bidAmount) >= 0)
                        {
                            Bid bid = new Bid();
                            bid.setProjectId(id);
                            bid.setName(name);
                            bid.setBidAmount(bidAmount);
                            bid.setContactEmail(contactEmail);

                            bidRepo.saveAndFlush(bid);

                            boolean newLowestBid = false;

                            // Check if this bid is the new winning bid
                            Bid lowestBid = project.getLowestBid();
                            if (lowestBid == null || lowestBid.getBidAmount().compareTo(bidAmount) > 0)
                            {
                                project.setLowestBid(bid);
                                projectRepo.saveAndFlush(project);

                                newLowestBid = true;
                            }

                            response.put("status", "success");
                            response.put("lowest_bid", newLowestBid + "");
                            response.put("bid_id", bid.getId().toString());
                        }
                        else
                        { // The bid is higher than the maximum budget for the project
                            success = false;

                            response.put("status", "error");

                            ProjectController.addMessage(response, "Your bid is higher than the maximum budget for this project");
                        }
                    }
                }
                else
                { // It is after the project's close date. Reject the bid.
                    success = false;

                    response.put("status", "error");

                    ProjectController.addMessage(response, "The project is closed");
                }
            }
            else
            { // The project doesn't exist
                success = false;

                response.put("status", "error");

                ProjectController.addMessage(response, "The project could not be found");
            }
        }
        catch (Exception e)
        {
            success = false;

            response.put("status", "error");

            ProjectController.addMessage(response, e.getMessage());
        }

        return response;
    }

}
