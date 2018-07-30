package com.bala;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;


public class TimezoneDisplay {
	public static void main(String[] argv) {

		ZoneId zone = ZoneOffset.of("-5");
		//ZoneId zone = ZoneOffset.ofHours(-8);
		TimeZone tz = TimeZone.getTimeZone(zone);
		int rawoffset = tz.getRawOffset();
		String[] str = TimeZone.getAvailableIDs(rawoffset);
		List<String> strLst = Arrays.asList(str);
		System.out.println("zone ID " + strLst);
		zone = ZoneId.of(str[0]);

		DateTimeFormatter dft = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		LocalDateTime ldt = LocalDateTime.parse("20180311020000", dft);
		System.out.println("ldt " + ldt);
		ZonedDateTime zdt = ldt.atZone(zone);
		System.out.println("zdt LDt >>" + zdt);

		Instant ins = Instant.now();
		System.out.println(ins);
		zdt = ins.atZone(zone);
		System.out.println("zdt offDt>>" + zdt);

		Map<String, String> sortedMap = new LinkedHashMap<>();

		List<String> zoneList = new ArrayList<>(ZoneId.getAvailableZoneIds());

		// Get all ZoneIds
		Map<String, String> allZoneIds = getAllZoneIds(zoneList);

		// sort by value, descending order
		allZoneIds.entrySet().stream().sorted(Map.Entry.<String, String>comparingByValue().reversed())
				.forEachOrdered(e -> sortedMap.put(e.getKey(), e.getValue()));

		// print map
		/*sortedMap.forEach((k, v) -> {
			String out = String.format("%35s (UTC%s) %n", k, v);
			System.out.printf(out);
		});

		System.out.println("\nTotal Zone IDs " + sortedMap.size());*/

		
		List<String> tzLst = getTimeZoneList(OffsetBase.UTC);
		System.out.println(tzLst);
	}

	private static Map<String, String> getAllZoneIds(List<String> zoneList) {

		Map<String, String> result = new HashMap<>();

		LocalDateTime dt = LocalDateTime.now();

		for (String zoneId : zoneList) {

			ZoneId zone = ZoneId.of(zoneId);
			ZonedDateTime zdt = dt.atZone(zone);
			ZoneOffset zos = zdt.getOffset();
			//System.out.println("zone >> " + zone.toString() + " zos id>> " + zos.getId());

			// replace Z to +00:00
			String offset = zos.getId().replaceAll("Z", "+00:00");

			result.put(zone.toString(), offset);

		}

		return result;

	}

	public static List<String> getTimeZoneList(OffsetBase base) {
		  
	    LocalDateTime now = LocalDateTime.now();
	    return ZoneId.getAvailableZoneIds().stream()
	      .map(ZoneId::of)
	      .sorted(new ZoneComparator())
	      .map(id -> String.format(
	        "(%s%s) %s", 
	        base, getOffset(now, id), id.getId()))
	      .collect(Collectors.toList());
	}
	
	public enum OffsetBase {
	    GMT, UTC
	}
	
	private static String getOffset(LocalDateTime dateTime, ZoneId id) {
	    return dateTime
	      .atZone(id)
	      .getOffset()
	      .getId()
	      .replace("Z", "+00:00");
	}
}

class ZoneComparator implements Comparator<ZoneId> {
	 
    @Override
    public int compare(ZoneId zoneId1, ZoneId zoneId2) {
        LocalDateTime now = LocalDateTime.now();
        ZoneOffset offset1 = now.atZone(zoneId1).getOffset();
        ZoneOffset offset2 = now.atZone(zoneId2).getOffset();
 
        return offset1.compareTo(offset2);
    }

}
