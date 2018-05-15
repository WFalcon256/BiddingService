package testproject.biddingservice.jpa;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "PROJECT", schema = "BIDDING")
public class Project implements Serializable
{

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private BigInteger id;

    @Column(name = "PROJECT_NAME")
    @Size(max = 45)
    @NotNull
    private String     projectName;

    @Column(name = "DESCRIPTION")
    @Size(max = 300)
    private String     description;

    @Column(name = "CONTACT_EMAIL")
    @Size(max = 100)
    @NotNull
    private String     contactEmail;

    @Column(name = "REQUIREMENTS_URL")
    @Size(max = 100)
    private String     requirementsUrl;

    @Column(name = "MAX_BUDGET")
    private BigDecimal maxBudget;

    @Column(name = "CLOSE_DATE")
    @NotNull
    private Timestamp  closeDate;

    @JoinColumn(name = "LOWEST_BID")
    @ManyToOne
    private Bid        lowestBid;

    public BigInteger getId()
    {
        return id;
    }

    public void setId(BigInteger id)
    {
        this.id = id;
    }

    public String getProjectName()
    {
        return projectName;
    }

    public void setProjectName(String projectName)
    {
        this.projectName = projectName;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getContactEmail()
    {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail)
    {
        this.contactEmail = contactEmail;
    }

    public String getRequirementsUrl()
    {
        return requirementsUrl;
    }

    public void setRequirementsUrl(String requirementsUrl)
    {
        this.requirementsUrl = requirementsUrl;
    }

    public BigDecimal getMaxBudget()
    {
        return maxBudget;
    }

    public void setMaxBudget(BigDecimal maxBudget)
    {
        this.maxBudget = maxBudget;
    }

    public Timestamp getCloseDate()
    {
        return closeDate;
    }

    public void setCloseDate(Timestamp closeDate)
    {
        this.closeDate = closeDate;
    }

    public Bid getLowestBid()
    {
        return lowestBid;
    }

    public void setLowestBid(Bid lowestBid)
    {
        this.lowestBid = lowestBid;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((closeDate == null) ? 0 : closeDate.hashCode());
        result = prime * result + ((contactEmail == null) ? 0 : contactEmail.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((lowestBid == null) ? 0 : lowestBid.hashCode());
        result = prime * result + ((maxBudget == null) ? 0 : maxBudget.hashCode());
        result = prime * result + ((projectName == null) ? 0 : projectName.hashCode());
        result = prime * result + ((requirementsUrl == null) ? 0 : requirementsUrl.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Project other = (Project)obj;
        if (closeDate == null)
        {
            if (other.closeDate != null) return false;
        }
        else if (!closeDate.equals(other.closeDate)) return false;
        if (contactEmail == null)
        {
            if (other.contactEmail != null) return false;
        }
        else if (!contactEmail.equals(other.contactEmail)) return false;
        if (description == null)
        {
            if (other.description != null) return false;
        }
        else if (!description.equals(other.description)) return false;
        if (id == null)
        {
            if (other.id != null) return false;
        }
        else if (!id.equals(other.id)) return false;
        if (lowestBid == null)
        {
            if (other.lowestBid != null) return false;
        }
        else if (!lowestBid.equals(other.lowestBid)) return false;
        if (maxBudget == null)
        {
            if (other.maxBudget != null) return false;
        }
        else if (!maxBudget.equals(other.maxBudget)) return false;
        if (projectName == null)
        {
            if (other.projectName != null) return false;
        }
        else if (!projectName.equals(other.projectName)) return false;
        if (requirementsUrl == null)
        {
            if (other.requirementsUrl != null) return false;
        }
        else if (!requirementsUrl.equals(other.requirementsUrl)) return false;
        return true;
    }

    @Override
    public String toString()
    {
        return "Project [id=" + id + ", projectName=" + projectName + ", description=" + description + ", contactEmail=" + contactEmail + ", requirementsUrl=" + requirementsUrl + ", maxBudget=" + maxBudget + ", closeDate=" + closeDate + ", lowestBid=" + lowestBid + "]";
    }

}
