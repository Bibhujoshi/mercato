package com.alten.mercato.server.model;

import java.util.HashSet;
import java.util.Set;

import net.sf.gilead.pojo.java5.LightEntity;

// Generated 2009-4-17 15:31:30 by Hibernate Tools 3.2.4.CR1

/**
 * Transfert generated by hbm2java
 */
public class Transfert extends LightEntity implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6966133960313497382L;
	private long transId;
	private String transExecId;
	private Personne consultant;
	private Departement depEntr;
	private Set<Personne> personnes = new HashSet<Personne>(0);

	public Transfert() {
	}
	
	public Transfert(Personne consultant, Departement depEntr) {
		this.consultant = consultant;
		this.depEntr = depEntr;
	}

	public Transfert(long transId, Personne consultant, Departement depEntr) {
		this.transId = transId;
		this.consultant = consultant;
		this.depEntr = depEntr;
	}

	public Transfert(long transId, String transExecId, Personne consultant, Departement depEntr,
			long transDepConsulId, Set<Personne> personnes) {
		this.transId = transId;
		this.transExecId = transExecId;
		this.consultant  = consultant;
		this.depEntr = depEntr;
		this.personnes = personnes;
	}

	public long getTransId() {
		return this.transId;
	}

	public void setTransId(long transId) {
		this.transId = transId;
	}

	public String getTransExecId() {
		return this.transExecId;
	}

	public void setTransExecId(String transExecId) {
		this.transExecId = transExecId;
	}

	/**
	 * @return the consultant
	 */
	public Personne getConsultant() {
		return consultant;
	}

	/**
	 * @param consultant the consultant to set
	 */
	public void setConsultant(Personne consultant) {
		this.consultant = consultant;
	}

	/**
	 * @return the depEntr
	 */
	public Departement getDepEntr() {
		return depEntr;
	}

	/**
	 * @param depEntr the depEntr to set
	 */
	public void setDepEntr(Departement depEntr) {
		this.depEntr = depEntr;
	}

	/**
	 * @return the personnes
	 */
	public Set<Personne> getPersonnes() {
		return personnes;
	}

	/**
	 * @param personnes the personnes to set
	 */
	public void setPersonnes(Set<Personne> personnes) {
		this.personnes = personnes;
	}


}
