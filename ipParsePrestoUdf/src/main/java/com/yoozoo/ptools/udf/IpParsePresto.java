package com.yoozoo.ptools.udf;

import com.facebook.presto.spi.function.Description;
import com.facebook.presto.spi.function.LiteralParameters;
import com.facebook.presto.spi.function.ScalarFunction;
import com.facebook.presto.spi.function.SqlType;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.*;
import io.airlift.slice.Slice;
import io.airlift.slice.Slices;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public final class IpParsePresto {
    private static DatabaseReader reader = null;
    private static List<String> charCoder = new ArrayList<>();


    static {
        try {
            InputStream is = IpParsePresto.class.getResourceAsStream("/" + "geoip.mmdb");
            reader = new DatabaseReader.Builder(is).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        charCoder.add("en");
        charCoder.add("zh-CN");
    }

    @Description("getlocation _FUNC_(ip) - return ip location if have ip location Otherwise return null.")
    @ScalarFunction("getlocation_v2")
    @LiteralParameters({"x"})
    @SqlType("varchar")
    public static Slice getlocation_v2(@SqlType("varchar(x)") Slice ip) {
        if (ip == null) {
            return Slices.utf8Slice("Error IP");
        }
        return getlocation_v2(ip, Slices.utf8Slice("zh-CN"));

    }

    @Description("getlocation _FUNC_(ip,charsetName) - return ip location if have ip location Otherwise return null.")
    @ScalarFunction("getlocation_v2")
    @LiteralParameters({"x"})
    @SqlType("varchar")
    public static Slice getlocation_v2(@SqlType("varchar(x)") Slice ip, @SqlType("varchar(x)") Slice charsetNameSlice) {
        String charsetName = charsetNameSlice.toStringUtf8();
        if (!charCoder.contains(charsetName)) {
            return Slices.utf8Slice("The encoding character does not support [Chinese (zh-CN), English (en)]");
        }

        if (ip == null) {
            return Slices.utf8Slice("Error IP");
        }
        String ipAdress = ip.toStringUtf8();
        InetAddress ipAddress = null;
        try {
            ipAddress = InetAddress.getByName(ipAdress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return Slices.utf8Slice("UnknownHost" + "(" + ipAdress + ")");
        }
        String charsetName_en = "en";
        StringBuilder sb = new StringBuilder("");
        try {
            CityResponse response = reader.city(ipAddress);
            //获取大陆
            Continent continent = response.getContinent();
            if (continent != null) {
                String continentName = continent.getNames().get(charsetName);
                sb.append(continentName != null ? continentName : continent.getNames().get(charsetName_en));
            }
            sb.append("|");
            //获取国家信息
            Country country = response.getCountry();
            if (country != null) {
                sb.append(country.getIsoCode());
            }
            sb.append("|");
            if (country != null) {
                sb.append(country.getGeoNameId());
            }
            sb.append("|");
            if (country != null) {
                String countryName = country.getNames().get(charsetName);
                sb.append(countryName != null ? countryName : country.getNames().get(charsetName_en));
            }
            sb.append("|");
            Subdivision subdivision = response.getMostSpecificSubdivision();
            if (subdivision != null) {
                String subdivisionName = subdivision.getNames().get(charsetName);
                sb.append(subdivisionName != null ? subdivisionName : subdivision.getNames().get(charsetName_en));
            }
            sb.append("|");
            City city = response.getCity();
            if (city != null) {
                String provinceName = city.getNames().get(charsetName);
                sb.append(provinceName != null ? provinceName : city.getNames().get(charsetName_en));
            }
            sb.append("|");
            sb.append("null");
            sb.append("|");
            Location location = response.getLocation();
//			获取经纬度，暂时不需要
            sb.append(location.getLongitude());
            sb.append("|");
            sb.append(location.getLatitude());
            sb.append("|");
            sb.append("null");
        } catch (Exception e) {
            e.printStackTrace();
            return Slices.utf8Slice("Error IP");
        } finally {
            System.out.println(sb.toString());
        }
        return Slices.utf8Slice(sb.toString());
    }


    public static void main(String[] args) {
        System.out.println(IpParsePresto.getlocation_v2(Slices.utf8Slice("61.174.15.215")));
    }

}
