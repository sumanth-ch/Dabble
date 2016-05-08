/**
 * 
 */
package com.fourvector.apps.dabble.model;

import java.io.Serializable;
import java.util.Date;

import com.fourvector.apps.dabble.model.user.UserModel;

/**
 * @author Anantha.Sharma
 */
public class BaseModel implements Serializable, Auditable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 8204924637088235774L;
	protected Integer			id;
	protected Integer			status;

	private Date		createdOn;
	private UserModel	createdBy;
	private Date		lastUpdatedOn;
	private UserModel	lastUpdatedBy;

	/**
	 * 
	 */
	public BaseModel() {
		super();
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(final Integer id) {
		this.id = id;
	}

	/**
	 * @return the status
	 */
	public Integer getStatus() {
		return this.status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(final Integer status) {
		this.status = status;
	}

	/**
	 * @return the createdOn
	 */
	public Date getCreatedOn() {
		return this.createdOn;
	}

	/**
	 * @param createdOn
	 *            the createdOn to set
	 */
	public void setCreatedOn(final Date createdOn) {
		this.createdOn = createdOn;
	}

	/**
	 * @return the createdBy
	 */
	public UserModel getCreatedBy() {
		return this.createdBy;
	}

	/**
	 * @param createdBy
	 *            the createdBy to set
	 */
	public void setCreatedBy(final UserModel createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return the lastUpdatedOn
	 */
	public Date getLastUpdatedOn() {
		return this.lastUpdatedOn;
	}

	/**
	 * @param lastUpdatedOn
	 *            the lastUpdatedOn to set
	 */
	public void setLastUpdatedOn(final Date lastUpdatedOn) {
		this.lastUpdatedOn = lastUpdatedOn;
	}

	/**
	 * @return the lastUpdatedBy
	 */
	public UserModel getLastUpdatedBy() {
		return this.lastUpdatedBy;
	}

	/**
	 * @param lastUpdatedBy
	 *            the lastUpdatedBy to set
	 */
	public void setLastUpdatedBy(final UserModel lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BaseModel other = (BaseModel) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		return true;
	}

}
