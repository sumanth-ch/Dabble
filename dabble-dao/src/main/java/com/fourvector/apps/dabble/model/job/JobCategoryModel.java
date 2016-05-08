/**
 * 
 */
package com.fourvector.apps.dabble.model.job;

import com.fourvector.apps.dabble.dao.config.DAOUtils;
import com.fourvector.apps.dabble.model.BaseModel;

/**
 * @author Anantha.Sharma
 */
public class JobCategoryModel extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1688475712818183485L;

	private String				name;
	private String				description;
	private JobCategoryModel	parent;

	/**
	 * 
	 */
	public JobCategoryModel() {
		super();
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * @return the parent
	 */
	public JobCategoryModel getParent() {
		return this.parent;
	}

	/**
	 * @param parent
	 *            the parent to set
	 */
	public void setParent(final JobCategoryModel parent) {
		this.parent = parent;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("JobCategoryModel [name=%s, description=%s, parent=%s, id=%s, status=%s]", this.name, this.description, DAOUtils.showEntity(this.parent), this.id, this.status);
	}

}
