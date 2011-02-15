package net._01001111.openidtester.openid;

import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.openid4java.association.Association;
import org.openid4java.association.AssociationException;
import org.openid4java.consumer.ConsumerException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryException;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.UrlIdentifier;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.AuthSuccess;
import org.openid4java.message.MessageException;
import org.openid4java.message.ParameterList;
import org.openid4java.server.IncrementalNonceGenerator;

@SuppressWarnings("rawtypes")
public class MockConsumerManager extends ConsumerManager {

	private String endpointUrl;

	public MockConsumerManager(String endpointUrl) throws ConsumerException {
		super();
		this.endpointUrl = endpointUrl;
	}

	public AuthRequest authenticate(DiscoveryInformation pDiscovered,
			String pReturnToUrl) throws MessageException, ConsumerException {
		return new AuthRequest(new ParameterList()) {
			public String getDestinationUrl(boolean pHttpGet) {
				return endpointUrl;
			}
		};
	}

	public List discover(String identifier) throws DiscoveryException {
		return Collections.EMPTY_LIST;
	}

	public DiscoveryInformation associate(List discoveries) {
		try {
			return new DiscoveryInformation(new URL(endpointUrl));
		} catch (Exception e) {
			return null;
		}
	}

	public VerificationResult verify(String receivingUrl,
			ParameterList response, DiscoveryInformation discovered)
			throws MessageException, DiscoveryException, AssociationException {
		VerificationResult verification = new VerificationResult();
		verification.setVerifiedId(new UrlIdentifier("http://localhost"));
		verification.setAuthResponse(AuthSuccess.createAuthSuccess(
				receivingUrl, "", "", false, receivingUrl,
				new IncrementalNonceGenerator().next(), "", Association
						.getFailedAssociation(new Date()), false));
		return verification;
	}
}