package org.teiath.data.dao;

import org.teiath.data.domain.User;
import org.teiath.data.domain.image.ApplicationImage;
import org.teiath.data.domain.trg.Listing;

import java.util.Collection;

public interface ApplicationImageDAO {

	public Collection<ApplicationImage> findByListing(Listing listing);

	public ApplicationImage findByUser(User user);

	public void save(ApplicationImage applicationImage);

	public void delete(ApplicationImage applicationImage);

	public void deleteAll(Listing listing);
}
