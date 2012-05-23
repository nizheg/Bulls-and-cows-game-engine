package info.nizheg.game.entity;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.repackaged.org.json.JSONException;
import com.google.appengine.repackaged.org.json.JSONObject;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Answer implements Serializable, Comparable<Answer> {

	private static final long serialVersionUID = 1L;
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.SEQUENCE)
	private Long id;
	@Persistent
	private Long userId;
	@Persistent
	private Long taskId;
	@Persistent
	private String answer;
	@Persistent
	private int bulls;
	@Persistent
	private int cows;
	@Persistent
	private int number;
	@Persistent
	private Date publishDate = new Date();

	public Answer(Long userId, Long taskId, String word) {
		this.answer = word;
		this.userId = userId;
		this.taskId = taskId;
	}

	public Answer() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public int getBulls() {
		return bulls;
	}

	public void setBulls(int bulls) {
		this.bulls = bulls;
	}

	public int getCows() {
		return cows;
	}

	public void setCows(int cows) {
		this.cows = cows;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public Date getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}

	public JSONObject toJSON() {
		try {
			JSONObject object = new JSONObject();
			object.put("answer", getAnswer());
			object.put("result", getBulls() + "-" + getCows());
			return object;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((answer == null) ? 0 : answer.hashCode());
		result = prime * result + ((taskId == null) ? 0 : taskId.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Answer))
			return false;
		Answer other = (Answer) obj;
		if (getAnswer() == null) {
			if (other.getAnswer() != null)
				return false;
		} else if (!getAnswer().equals(other.getAnswer()))
			return false;
		if (getTaskId() == null) {
			if (other.getTaskId() != null)
				return false;
		} else if (!getTaskId().equals(other.getTaskId()))
			return false;
		if (getUserId() == null) {
			if (other.getUserId() != null)
				return false;
		} else if (!getUserId().equals(other.getUserId()))
			return false;
		return true;
	}

	@Override
	public int compareTo(Answer another) {
		if (this.equals(another))
			return 0;
		return this.number - another.number;
	}
}
