// default package
// Generated 2009-4-10 12:28:00 by Hibernate Tools 3.2.4.CR1

package com.alten.mercato.server.model;

import java.util.HashSet;
import java.util.Set;

import net.sf.gilead.pojo.java5.LightEntity;

/**
 * TypePersonne generated by hbm2java
 */
public class TypePersonne extends LightEntity implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5119444147310370412L;
	private long tpersId;
	private String tpersCode;
	private String tpersLib;
	private Set<Personne> personnes = new HashSet<Personne>(0);

	public TypePersonne() {
	}

	public TypePersonne(long tpersId, String tpersCode, String tpersLib) {
		this.tpersId = tpersId;
		this.tpersCode = tpersCode;
		this.tpersLib = tpersLib;
	}

	public TypePersonne(long tpersId, String tpersCode, String tpersLib,
			Set<Personne> personnes) {
		this.tpersId = tpersId;
		this.tpersCode = tpersCode;
		this.tpersLib = tpersLib;
		this.personnes = personnes;
	}

	public long getTpersId() {
		return this.tpersId;
	}

	public void setTpersId(long tpersId) {
		this.tpersId = tpersId;
	}

	public String getTpersCode() {
		return this.tpersCode;
	}

	public void setTpersCode(String tpersCode) {
		this.tpersCode = tpersCode;
	}

	public String getTpersLib() {
		return this.tpersLib;
	}

	public void setTpersLib(String tpersLib) {
		this.tpersLib = tpersLib;
	}

	public Set<Personne> getPersonnes() {
		return this.personnes;
	}

	public void setPersonnes(Set<Personne> personnes) {
		this.personnes = personnes;
	}

}