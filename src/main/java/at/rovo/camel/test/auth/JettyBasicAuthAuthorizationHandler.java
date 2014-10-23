package at.rovo.camel.test.auth;

import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.DefaultIdentityService;
import org.eclipse.jetty.security.IdentityService;
import org.eclipse.jetty.security.LoginService;
import org.eclipse.jetty.security.MappedLoginService;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.UserIdentity;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.security.Credential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.Principal;

import javax.security.auth.Subject;

/**
 * Sets up a basic authentication mechanism for REST based services exposed via
 * Jetty for our REST API (http(s)://server:8383/api/v1/...).
 * <p/>
 * It moreover defines a login service which is capable of using an internal
 * persistence layer for authenticating a user and his credentials received via
 * a challenge response against a user entity retrieved via the persistence
 * layer.
 * 
 * @author Roman Vottner
 */
@Service
public class JettyBasicAuthAuthorizationHandler extends
		ConstraintSecurityHandler {

	/**
	 * The logger of this class
	 */
	private static final Logger LOG = LoggerFactory
			.getLogger(JettyBasicAuthAuthorizationHandler.class);

	/**
	 * The roles user can fulfill
	 */
	private final String[] roles = new String[] { "user" };

	/**
	 * Initializes a Jetty based Basic Authentication mechanism.
	 */
	public JettyBasicAuthAuthorizationHandler() {
		super();

		// Specifies the challenge to be of type BASIC and that users have
		// to fulfill one of the roles listed in roles. Moreover authentication
		// is required
		Constraint constraint = new Constraint();
		constraint.setName(Constraint.__BASIC_AUTH);
		constraint.setRoles(this.roles);
		constraint.setAuthenticate(true);

		// Map the defined constraints from above to the services provided via
		// our REST API
		ConstraintMapping cm = new ConstraintMapping();
		cm.setConstraint(constraint);
		cm.setPathSpec("/api/v1/*");

		// BasicAuthenticator takes care of sending a challenge to the caller
		// and calls our login service in case of a challenge response to
		// evaluate if the user is permitted to use the service.
		// The realm name defines the name of the login service which should be
		// used for authentication.
		BasicAuthenticator basic = new BasicAuthenticator();
		this.setAuthenticator(basic);
		this.addConstraintMapping(cm);
		this.setRealmName("REST");
		this.setLoginService(new BeanManagedLoginService("REST"));

		LOG.debug("JettyBasicAuthAuthorizationHandler created!");
	}

	/**
	 * Implements a bean managed login service where an authentication response
	 * is propagated to a business layer bean which retrieves the user and
	 * credentials from a backing data store.
	 * 
	 * @author Roman Vottner
	 */
	class BeanManagedLoginService implements LoginService {

		/**
		 * An identity service used to create a UserIdentity object for us
		 */
		private IdentityService identityService = new DefaultIdentityService();

		private String name = "REST";

		/**
		 * Initializes a new instance and sets the realm name this login service
		 * will work for.
		 * 
		 * @param name
		 *            The name of this login service (also known as the realm it
		 *            will work for)
		 */
		public BeanManagedLoginService(String name) {
			this.name = name;
		}

		/**
		 * Returns the name of the login service (the realm name)
		 * 
		 * @return Get the name of the login service (aka Realm name)
		 */
		@Override
		public String getName() {
			return this.name;
		}

		/**
		 * Logs in a user by checking the username with known users and
		 * comparing the credentials with the stored ones. If the user could not
		 * be authenticated successfully an unauthenticated user identity will
		 * be returned.
		 * 
		 * @param username
		 *            The user name as sent by the ChallengeResponse
		 * @param credentials
		 *            The credentials provided in the ChallengeResponse
		 * @return If the user could be authenticated successfully a valid
		 *         {@link UserIdentity}
		 */
		@Override
		public UserIdentity login(String username, Object credentials) {
			if (LOG.isDebugEnabled()) {
				LOG.debug(
						"received login request for user: '{}' with credentials: '{}'!",
						username, credentials);
			}

			// check if the username is valid
			if (null != username && !"".equals(username)) {
				String password = credentials.toString();

				// retrieve the user from the business layer
				// simplified for the example
				if (!"admin".equals(username)) {
					if (LOG.isErrorEnabled()) {
						LOG.error(
								"No User could be found for UserId '{}'. The UserKey (which was not checked) is '{}'",
								username, password);
					}
					return UserIdentity.UNAUTHENTICATED_IDENTITY;
				}
				// check whether the password matches the one in the user entity
				// found for the user id
				if (password.equals("secret")) {
					// the user could get authenticated successfully
					if (LOG.isDebugEnabled()) {
						LOG.debug(
								"UserKey {} of User {} was successfully authenticated",
								password, username);
					}
					return this.createIdentityForUser(username, password);
				} else {
					// the password set in the request and the one stored in the
					// user entity do not match
					if (LOG.isErrorEnabled()) {
						LOG.error(
								"User {} did not authenticate successfully. Provided userkey was {}",
								username, password);
					}
					return UserIdentity.UNAUTHENTICATED_IDENTITY;
				}
			} else {
				if (LOG.isErrorEnabled()) {
					LOG.error("Username is empty and therefore could not get authenticated correctly");
				}
				return UserIdentity.UNAUTHENTICATED_IDENTITY;
			}
		}

		/**
		 * Creates a UserIdentity object for a successfully authenticated user.
		 * 
		 * @param username
		 *            The name of the authenticated user
		 * @param password
		 *            The password of the authenticated user
		 * @return A valid UserIdentity object
		 */
		private UserIdentity createIdentityForUser(String username,
				String password) {
			// create a principal object needed for the user identity
			Credential cred = Credential.getCredential(password);
			// a principal is basically an identity of a real person
			// (subject). So a user can have multiple principals
			Principal userPrincipal = new MappedLoginService.KnownUser(
					username, cred);

			// a subject collects all data necessary to identify a certain
			// person. It may store multiple identities and passwords or
			// cryptographic keys
			Subject subject = new Subject();
			// add a Principal and credential to the Subject
			subject.getPrincipals().add(userPrincipal);
			subject.getPrivateCredentials().add(cred);
			subject.setReadOnly();

			return this.identityService.newUserIdentity(subject, userPrincipal,
					roles);
		}

		/**
		 * Validate just checks if a user identity is still valid.
		 */
		@Override
		public boolean validate(UserIdentity user) {
			return true;
		}

		@Override
		public IdentityService getIdentityService() {
			return this.identityService;
		}

		@Override
		public void setIdentityService(IdentityService service) {
			this.identityService = service;
		}

		@Override
		public void logout(UserIdentity user) {

		}
	}
}