package gov.usgs.earthquake.event;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for EventIDAssociator.
 */
public class EventIDAssociatorTest {

	private EventIDAssociator testAssociator;
	private TestEventWebService testService;

	@Before
	public void setup() {
		testService = new TestEventWebService();
		testAssociator = new EventIDAssociator(testService,
				EventIDAssociator.DEFAULT_TIME_DIFFERENCE,
				EventIDAssociator.DEFAULT_LOCATION_DIFFERENCE);
	}

	/**
	 * Check the nearby queries generated by event id associator match
	 * expectations.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testNearbyQuery() throws Exception {
		DefaultEventInfo event = new DefaultEventInfo();
		event.setTime(new Date());
		event.setLatitude(new BigDecimal("34"));
		event.setLongitude(new BigDecimal("-118"));

		EventQuery query;

		testAssociator.getNearbyEvents(event, null);
		query = testService.lastQuery;
		Assert.assertEquals("expected start time", query.getStartTime(), new Date(
				event.getTime().getTime() - EventIDAssociator.DEFAULT_TIME_DIFFERENCE));
		Assert.assertEquals("expected end time", query.getEndTime(), new Date(event
				.getTime().getTime() + EventIDAssociator.DEFAULT_TIME_DIFFERENCE));
		Assert.assertEquals("expected latitude", event.getLatitude(),
				query.getLatitude());
		Assert.assertEquals("expected longitude", event.getLongitude(),
				query.getLongitude());
		Assert.assertEquals("expected radius", new BigDecimal("100").divide(
				EventIDAssociator.KILOMETERS_PER_DEGREE, MathContext.DECIMAL32), query
				.getMaxRadius());
		Assert.assertNull("expected null network", query.getCatalog());

		testAssociator.getNearbyEvents(event, "testnetwork");
		query = testService.lastQuery;
		Assert.assertEquals("expected null network", "testnetwork",
				query.getCatalog());
	}

	/**
	 * Test event web service that returns empty list of events, and captures
	 * queries for inspection.
	 */
	public static class TestEventWebService extends EventWebService {

		public TestEventWebService() {
			super(null);
		}

		public EventQuery lastQuery = null;

		@Override
		public List<JsonEvent> getEvents(final EventQuery query) {
			this.lastQuery = query;
			return new ArrayList<JsonEvent>();
		}

	}

}