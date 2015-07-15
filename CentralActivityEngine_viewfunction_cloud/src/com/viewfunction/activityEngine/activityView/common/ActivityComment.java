package com.viewfunction.activityEngine.activityView.common;

import java.util.Date;

import com.viewfunction.activityEngine.security.Participant;
import com.viewfunction.activityEngine.security.Role;

public class ActivityComment {
	private Participant  participant;
	private Role role;
	private Date addDate;
	private String commentContent;
	public Participant getParticipant() {
		return participant;
	}
	public void setParticipant(Participant participant) {
		this.participant = participant;
	}
	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}
	public Date getAddDate() {
		return addDate;
	}
	public void setAddDate(Date addDate) {
		this.addDate = addDate;
	}
	public String getCommentContent() {
		return commentContent;
	}
	public void setCommentContent(String commentContent) {
		this.commentContent = commentContent;
	}
}
