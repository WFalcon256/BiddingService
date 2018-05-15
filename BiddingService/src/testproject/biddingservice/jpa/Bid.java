package testproject.biddingservice.jpa;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "BIDS", schema = "BIDDING")
public class Bid implements Serializable
{

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private BigInteger id;

    @Column(name = "PROJECT_ID")
    @NotNull
    private BigInteger projectId;

    @Column(name = "BID_AMOUNT")
    @NotNull
    private BigDecimal bidAmount;

    @Column(name = "CONTACT_EMAIL")
    @Size(max = 100)
    @NotNull
    private String     contactEmail;

    @Column(name = "NAME")
    @Size(max = 45)
    private String     name;

    public BigInteger getId()
    {
        return id;
    }

    public void setId(BigInteger id)
    {
        this.id = id;
    }

    public BigInteger getProjectId()
    {
        return projectId;
    }

    public void setProjectId(BigInteger projectId)
    {
        this.projectId = projectId;
    }

    public BigDecimal getBidAmount()
    {
        return bidAmount;
    }

    public void setBidAmount(BigDecimal bidAmount)
    {
        this.bidAmount = bidAmount;
    }

    public String getContactEmail()
    {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail)
    {
        this.contactEmail = contactEmail;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((bidAmount == null) ? 0 : bidAmount.hashCode());
        result = prime * result + ((contactEmail == null) ? 0 : contactEmail.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((projectId == null) ? 0 : projectId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Bid other = (Bid)obj;
        if (bidAmount == null)
        {
            if (other.bidAmount != null) return false;
        }
        else if (!bidAmount.equals(other.bidAmount)) return false;
        if (contactEmail == null)
        {
            if (other.contactEmail != null) return false;
        }
        else if (!contactEmail.equals(other.contactEmail)) return false;
        if (id == null)
        {
            if (other.id != null) return false;
        }
        else if (!id.equals(other.id)) return false;
        if (name == null)
        {
            if (other.name != null) return false;
        }
        else if (!name.equals(other.name)) return false;
        if (projectId == null)
        {
            if (other.projectId != null) return false;
        }
        else if (!projectId.equals(other.projectId)) return false;
        return true;
    }

    @Override
    public String toString()
    {
        return "Bid [id=" + id + ", projectId=" + projectId + ", bidAmount=" + bidAmount + ", contactEmail=" + contactEmail + ", name=" + name + "]";
    }

}
