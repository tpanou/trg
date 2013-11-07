package org.teiath.webservices.viewers;

import com.sun.syndication.feed.rss.Channel;
import com.sun.syndication.feed.rss.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.view.feed.AbstractRssFeedView;
import org.teiath.data.properties.EmailProperties;
import org.teiath.webservices.model.ServiceProduct;
import org.teiath.webservices.model.ServiceProductList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RssViewer
		extends AbstractRssFeedView {

	@Autowired
	private EmailProperties emailProperties;

	@Override
	protected void buildFeedMetadata(Map<String, Object> model, Channel feed, HttpServletRequest request) {
		feed.setTitle("RSS Web Service");
		feed.setDescription("Feed provided by T.E.I of Athens");
		feed.setLink("www.teiath.gr");
	}

	@Override
	protected List<Item> buildFeedItems(Map<String, Object> model, HttpServletRequest request,
	                                    HttpServletResponse response)
			throws Exception {

		List<Item> items = new ArrayList<>();
		response.setContentType("application/xml;charset=UTF-8");

		if (model.get("serviceProductList") instanceof ServiceProductList) {
			ServiceProductList serviceProductList = (ServiceProductList) model.get("serviceProductList");

			if (serviceProductList != null) {
				// Create feed items
				Item item;
				for (ServiceProduct serviceProduct : serviceProductList.getServiceProducts()) {
					item = new Item();
					item.setAuthor(serviceProduct.getOwnerName());
					item.setTitle(serviceProduct.getTransactionTypeName() + ": " + serviceProduct.getProductName());
					item.setPubDate(serviceProduct.getListingCreationDate());
					item.setLink(emailProperties.getDomain()+"previewAd?code="+serviceProduct.getCode());
					items.add(item);
				}
			}
		}

		if (model.get("serviceProduct") instanceof ServiceProduct) {
			ServiceProduct serviceProduct = (ServiceProduct) model.get("serviceProduct");

			if (serviceProduct != null) {
				// Create feed items
				Item item;
				item = new Item();
				item.setAuthor(serviceProduct.getOwnerName());
				item.setTitle(serviceProduct.getTransactionTypeName() + ": " + serviceProduct.getProductName());
				item.setPubDate(serviceProduct.getListingCreationDate());
				item.setLink(emailProperties.getDomain()+"previewAd?code="+serviceProduct.getCode());
				items.add(item);
			}
		}

		return items;
	}
}
