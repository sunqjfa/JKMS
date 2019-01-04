/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jyms.data;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author John
 */
@Entity
@Table(name = "CLIENTLOG", catalog = "", schema = "APP")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Clientlog.findAll", query = "SELECT c FROM Clientlog c"),
    @NamedQuery(name = "Clientlog.findById", query = "SELECT c FROM Clientlog c WHERE c.id = :id"),
    @NamedQuery(name = "Clientlog.findByOperationtime", query = "SELECT c FROM Clientlog c WHERE c.operationtime = :operationtime"),
    @NamedQuery(name = "Clientlog.findByUsernameOper", query = "SELECT c FROM Clientlog c WHERE c.usernameOper = :usernameOper"),
    @NamedQuery(name = "Clientlog.findByLogtype", query = "SELECT c FROM Clientlog c WHERE c.logtype = :logtype"),
    @NamedQuery(name = "Clientlog.findByDescription", query = "SELECT c FROM Clientlog c WHERE c.description = :description"),
    @NamedQuery(name = "Clientlog.findBySerialnoOper", query = "SELECT c FROM Clientlog c WHERE c.serialnoOper = :serialnoOper"),
    @NamedQuery(name = "Clientlog.findByGroupnameOper", query = "SELECT c FROM Clientlog c WHERE c.groupnameOper = :groupnameOper"),
    @NamedQuery(name = "Clientlog.findByNodenameOper", query = "SELECT c FROM Clientlog c WHERE c.nodenameOper = :nodenameOper"),
    @NamedQuery(name = "Clientlog.findBySerialnojoin", query = "SELECT c FROM Clientlog c WHERE c.serialnojoin = :serialnojoin"),
    @NamedQuery(name = "Clientlog.findByChanneljoin", query = "SELECT c FROM Clientlog c WHERE c.channeljoin = :channeljoin"),
    @NamedQuery(name = "Clientlog.findByDvrtypeOper", query = "SELECT c FROM Clientlog c WHERE c.dvrtypeOper = :dvrtypeOper"),
    @NamedQuery(name = "Clientlog.findByObjecttypeOper", query = "SELECT c FROM Clientlog c WHERE c.objecttypeOper = :objecttypeOper"),
    @NamedQuery(name = "Clientlog.findByRemarks", query = "SELECT c FROM Clientlog c WHERE c.remarks = :remarks")})
public class Clientlog implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "OPERATIONTIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date operationtime;
    @Basic(optional = false)
    @Column(name = "USERNAME_OPER")
    private String usernameOper;
    @Basic(optional = false)
    @Column(name = "LOGTYPE")
    private String logtype;
    @Basic(optional = false)
    @Column(name = "DESCRIPTION")
    private String description;
    @Basic(optional = false)
    @Column(name = "SERIALNO_OPER")
    private String serialnoOper;
    @Column(name = "GROUPNAME_OPER")
    private String groupnameOper;
    @Column(name = "NODENAME_OPER")
    private String nodenameOper;
    @Column(name = "SERIALNOJOIN")
    private String serialnojoin;
    @Column(name = "CHANNELJOIN")
    private String channeljoin;
    @Column(name = "DVRTYPE_OPER")
    private String dvrtypeOper;
    @Column(name = "OBJECTTYPE_OPER")
    private String objecttypeOper;
    @Column(name = "REMARKS")
    private String remarks;

    public Clientlog() {
    }

    public Clientlog(Integer id) {
        this.id = id;
    }

    public Clientlog(Integer id, Date operationtime, String usernameOper, String logtype, String description, String serialnoOper) {
        this.id = id;
        this.operationtime = operationtime;
        this.usernameOper = usernameOper;
        this.logtype = logtype;
        this.description = description;
        this.serialnoOper = serialnoOper;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getOperationtime() {
        return operationtime;
    }

    public void setOperationtime(Date operationtime) {
        this.operationtime = operationtime;
    }

    public String getUsernameOper() {
        return usernameOper;
    }

    public void setUsernameOper(String usernameOper) {
        this.usernameOper = usernameOper;
    }

    public String getLogtype() {
        return logtype;
    }

    public void setLogtype(String logtype) {
        this.logtype = logtype;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSerialnoOper() {
        return serialnoOper;
    }

    public void setSerialnoOper(String serialnoOper) {
        this.serialnoOper = serialnoOper;
    }

    public String getGroupnameOper() {
        return groupnameOper;
    }

    public void setGroupnameOper(String groupnameOper) {
        this.groupnameOper = groupnameOper;
    }

    public String getNodenameOper() {
        return nodenameOper;
    }

    public void setNodenameOper(String nodenameOper) {
        this.nodenameOper = nodenameOper;
    }

    public String getSerialnojoin() {
        return serialnojoin;
    }

    public void setSerialnojoin(String serialnojoin) {
        this.serialnojoin = serialnojoin;
    }

    public String getChanneljoin() {
        return channeljoin;
    }

    public void setChanneljoin(String channeljoin) {
        this.channeljoin = channeljoin;
    }

    public String getDvrtypeOper() {
        return dvrtypeOper;
    }

    public void setDvrtypeOper(String dvrtypeOper) {
        this.dvrtypeOper = dvrtypeOper;
    }

    public String getObjecttypeOper() {
        return objecttypeOper;
    }

    public void setObjecttypeOper(String objecttypeOper) {
        this.objecttypeOper = objecttypeOper;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Clientlog)) {
            return false;
        }
        Clientlog other = (Clientlog) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jyms.data.Clientlog[ id=" + id + " ]";
    }
    
}
