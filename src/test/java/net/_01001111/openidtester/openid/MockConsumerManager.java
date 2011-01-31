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

public class MockConsumerManager extends ConsumerManager {

	private String endpointUrl;

	public MockConsumerManager(String pEndpointUrl) throws ConsumerException {
		super();
		this.endpointUrl = pEndpointUrl;
	}

	public AuthRequest authenticate(DiscoveryInformation pDiscovered,
			String pReturnToUrl) throws MessageException, ConsumerException {
		return new AuthRequest(new ParameterList()) {
			public String getDestinationUrl(boolean pHttpGet) {
				return endpointUrl;
			}
		};
	}

	@SuppressWarnings("unchecked")
	public List discover(String pIdentifier) throws DiscoveryException {
		return Collections.EMPTY_LIST;
	}

	@SuppressWarnings("unchecked")
	public DiscoveryInformation associate(List pDiscoveries) {
		try {
			return new DiscoveryInformation(new URL(endpointUrl));
		} catch (Exception e) {
			return null;
		}
	}

	public VerificationResult verify(String pReceivingUrl,
			ParameterList pResponse, DiscoveryInformation pDiscovered)
			throws MessageException, DiscoveryException, AssociationException {
		VerificationResult verification = new VerificationResult();
		verification.setVerifiedId(new UrlIdentifier("http://localhost"));
		verification.setAuthResponse(AuthSuccess.createAuthSuccess(
				pReceivingUrl, "", "", false, pReceivingUrl,
				new IncrementalNonceGenerator().next(), "", Association
						.getFailedAssociation(new Date()), false));
		return verification;
	}
}