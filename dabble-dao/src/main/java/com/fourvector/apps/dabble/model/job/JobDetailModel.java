/**
 * 
 */
package com.fourvector.apps.dabble.model.job;

import com.fourvector.apps.dabble.model.BaseModel;

/**
 * @author asharma
 */
public class JobDetailModel extends BaseModel {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1082842689394111003L;
	private String				artifactUrl;
	private Integer				artifactType;
	private JobModel			job;

	/**
	 * 
	 */
	public JobDetailModel() {
		super();
	}

	/**
	 * @param artifactUrl
	 * @param artifactType
	 */
	public JobDetailModel(String artifactUrl, Integer artifactType) {
		super();
		this.artifactUrl = artifactUrl;
		this.artifactType = artifactType;
	}

	/**
	 * @return the artifactUrl
	 */
	public String getArtifactUrl() {
		return artifactUrl;
	}

	/**
	 * @param artifactUrl
	 *            the artifactUrl to set
	 */
	public void setArtifactUrl(String artifactUrl) {
		this.artifactUrl = artifactUrl;
	}

	/**
	 * @return the artifactType
	 */
	public Integer getArtifactType() {
		return artifactType;
	}

	/**
	 * @param artifactType
	 *            the artifactType to set
	 */
	public void setArtifactType(Integer artifactType) {
		this.artifactType = artifactType;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("JobDetailModel [artifactUrl=%s, artifactType=%s]", artifactUrl, artifactType);
	}

	/**
	 * @return the job
	 */
	public JobModel getJob() {
		return job;
	}

	/**
	 * @param job
	 *            the job to set
	 */
	public void setJob(JobModel job) {
		this.job = job;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((artifactType == null) ? 0 : artifactType.hashCode());
		result = prime * result + ((artifactUrl == null) ? 0 : artifactUrl.hashCode());
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
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		JobDetailModel other = (JobDetailModel) obj;
		if (artifactType == null) {
			if (other.artifactType != null)
				return false;
		} else if (!artifactType.equals(other.artifactType))
			return false;
		if (artifactUrl == null) {
			if (other.artifactUrl != null)
				return false;
		} else if (!artifactUrl.equals(other.artifactUrl))
			return false;
		return true;
	}

}
