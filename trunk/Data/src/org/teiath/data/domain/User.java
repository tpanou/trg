package org.teiath.data.domain;

import org.teiath.data.domain.image.ApplicationImage;
import twitter4j.auth.AccessToken;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;

@Entity
@Table(name = "com_users")
public class User
		implements Serializable {

	public final static int GENDER_MALE = 0;
	public final static int GENDER_FEMALE = 1;

	public final static int USER_TYPE_EXTERNAL = 0;
	public final static int USER_TYPE_STUDENT = 1;
	public final static int USER_TYPE_PROFESSOR = 2;
	public final static int USER_TYPE_ADMINISTRATION_CLERK = 3;
	public final static int USER_TYPE_GRADUATE = 4;
	public final static int USER_TYPE_STAFF = 5;
	public final static int USER_TYPE_AFFILIATE = 6;

	public final static int USER_ROLE_ADMINISTRATION_CLERK = 0;
	public final static int USER_ROLE_ADMINISTRATOR =1;
	public final static int USER_ROLE_SIMPLE_USER = 2;

	@Id
	@Column(name = "user_id")
	@SequenceGenerator(name = "users_seq", sequenceName = "com_users_user_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq")
	private Integer id;

	@Column(name = "user_lastname", length = 100, nullable = false)
	private String lastName;
	@Column(name = "user_firstname", length = 100, nullable = false)
	private String firstName;
	@Column(name = "user_username", length = 20, nullable = false)
	private String userName;
	@Column(name = "user_password", length = 50, nullable = true)
	private String password;
	@Column(name = "user_birth_date", nullable = true)
	private Date birthDate;
	@Column(name = "user_email", length = 100, nullable = false)
	private String email;
	@Column(name = "user_home_address", length = 1000, nullable = true)
	private String homeAddress;
	@Column(name = "user_phoneNumber", nullable = true)
	private BigInteger phoneNumber;
	@Column(name = "user_mobileNumber", nullable = true)
	private BigInteger mobileNumber;
	@Column(name = "user_faxNumber", nullable = true)
	private BigInteger faxNumber;
	@Column(name = "user_facebook_id", length = 100, nullable = true)
	private String facebookId;
	@Column(name = "user_google_id", length = 100, nullable = true)
	private String googleId;
	@Column(name = "user_twitter_id", length = 100, nullable = true)
	private String twitterId;
	@Column(name = "user_twitter_access_token", nullable = true)
	private AccessToken twitterAccessToken;
	@Column(name = "user_gender", nullable = true)
	private Integer gender;
	@Column(name = "user_type", nullable = false)
	private Integer userType;
	@Column(name = "user_registration_date", nullable = false)
	private Date registrationDate;
	@Column(name = "user_is_unemployed", nullable = true)
	private boolean unemployed;
	@Column(name = "user_is_amea", nullable = true)
	private boolean amea;
	@Column(name = "user_is_approved", nullable = false)
	private boolean approved;
	@Column(name = "user_reset_token", nullable = true)
	private String resetToken;
	@Column(name = "user_reset_expiration", nullable = true)
	private Date resetExpiration;

	@OneToOne(cascade = CascadeType.ALL)
	private ApplicationImage applicationImage;

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "USER_USER_ROLES", joinColumns = {@JoinColumn(name = "user_id")},
			inverseJoinColumns = {@JoinColumn(name = "user_role_id")})
	private Collection<UserRole> roles;

	private String fullName;
	private Double averageTransactionRating;

	public User() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getFullName() {
		return this.getFirstName() + " " + this.getLastName();
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public ApplicationImage getApplicationImage() {
		return applicationImage;
	}

	public void setApplicationImage(ApplicationImage applicationImage) {
		this.applicationImage = applicationImage;
	}

	public Double getAverageTransactionRating() {
		return averageTransactionRating;
	}

	public void setAverageTransactionRating(Double averageTransactionRating) {
		this.averageTransactionRating = averageTransactionRating;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public String getHomeAddress() {
		return homeAddress;
	}

	public void setHomeAddress(String homeAddress) {
		this.homeAddress = homeAddress;
	}

	public String getFacebookId() {
		return facebookId;
	}

	public void setFacebookId(String facebookId) {
		this.facebookId = facebookId;
	}

	public String getGoogleId() {
		return googleId;
	}

	public void setGoogleId(String googleId) {
		this.googleId = googleId;
	}

	public String getTwitterId() {
		return twitterId;
	}

	public void setTwitterId(String twitterId) {
		this.twitterId = twitterId;
	}

	public Integer getUserType() {
		return userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public BigInteger getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(BigInteger phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public BigInteger getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(BigInteger mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public BigInteger getFaxNumber() {
		return faxNumber;
	}

	public void setFaxNumber(BigInteger faxNumber) {
		this.faxNumber = faxNumber;
	}

	public Date getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}

	public Collection<UserRole> getRoles() {
		return roles;
	}

	public void setRoles(Collection<UserRole> roles) {
		this.roles = roles;
	}

	public AccessToken getTwitterAccessToken() {
		return twitterAccessToken;
	}

	public void setTwitterAccessToken(AccessToken twitterAccessToken) {
		this.twitterAccessToken = twitterAccessToken;
	}

	public boolean isUnemployed() {
		return unemployed;
	}

	public void setUnemployed(boolean unemployed) {
		this.unemployed = unemployed;
	}

	public boolean isAmea() {
		return amea;
	}

	public void setAmea(boolean amea) {
		this.amea = amea;
	}

	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	public String getResetToken() {
		return resetToken;
	}

	public void setResetToken(String resetToken) {
		this.resetToken = resetToken;
	}

	public Date getResetExpiration() {
		return resetExpiration;
	}

	public void setResetExpiration(Date resetExpiration) {
		this.resetExpiration = resetExpiration;
	}
}
