// src/main/java/com/example/domo/service/PlaceRowMapper.java
package com.example.domo.service;

import com.example.domo.model.Place;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PlaceRowMapper implements RowMapper<Place> {
    @Override
    public Place mapRow(ResultSet rs, int rowNum) throws SQLException {
        Place p = new Place();
        p.setName(rs.getString("name"));
        p.setCategory(rs.getString("category"));
        p.setAddress(rs.getString("address"));
        p.setLat(rs.getDouble("lat"));
        p.setLng(rs.getDouble("lng"));
        p.setSido(rs.getString("sido"));
        p.setSigungu(rs.getString("sigungu"));
        p.setDiscountPercent(rs.getInt("discountpercent"));
        // popularity 컬럼이 없으면 coalesce(0)로 가져오도록 SQL에서 처리
        p.setPopularity(rs.getInt("popularity"));
        return p;
    }
}
