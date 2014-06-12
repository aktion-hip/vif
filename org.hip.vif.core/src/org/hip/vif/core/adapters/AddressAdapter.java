/*
 This package is part of the application VIF.
 Copyright (C) 2010, Benno Luthiger

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.hip.vif.core.adapters;

import java.util.Collection;
import java.util.Vector;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.mail.MailGenerationException;

/**
 * Adapter class for <code>javax.mail.internet.InternetAddress</code>.
 *
 * @author Luthiger
 * Created: 03.06.2010
 */
public class AddressAdapter {
	private InternetAddress internetAddress;

	public AddressAdapter(InternetAddress inAddress) {
		internetAddress = inAddress;
	}
	
	public InternetAddress getInternetAddress() {
		return internetAddress;
	}

	/**
	 * Parses the passed string containing mail addresses into a collection of mail addresses.
	 * 
	 * @param inAddressList String comma separated list of mail addresses
	 * @return Collection<AddressAdapter>
	 * @throws MailGenerationException
	 */
	public static Collection<AddressAdapter> parse(String inAddressList) throws MailGenerationException {
		Collection<AddressAdapter> outAddresses = new Vector<AddressAdapter>();
		try {
			for (InternetAddress lAddress : InternetAddress.parse(inAddressList)) {
				outAddresses.add(new AddressAdapter(lAddress));
			}
		} catch (AddressException exc) {
			throw new MailGenerationException(exc.getMessage());
		}
		return outAddresses;
	}
	
	/**
	 * Evaluates the passed result set to create a collection of mail addresses.
	 * 
	 * @param inResult {@link QueryResult}
	 * @param inFieldName String the name of the field containing the mail address
	 * @return Collection<AddressAdapter>
	 * @throws MailGenerationException
	 */
	public static Collection<AddressAdapter> fill(QueryResult inResult, String inFieldName) throws MailGenerationException {
		Collection<AddressAdapter> outAddresses = new Vector<AddressAdapter>();
		try {			
			while (inResult.hasMoreElements()) {
				outAddresses.add(new AddressAdapter(new InternetAddress(inResult.next().get(inFieldName).toString())));
			}
		}
		catch (Exception exc) {
			throw new MailGenerationException(exc.getMessage());			
		}
		return outAddresses;
	}
	
}
