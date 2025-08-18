package com.example.domo.repository;

import com.example.domo.model.Place;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class PlaceRepository {

    private final NamedParameterJdbcTemplate jdbc;

    // 단일 RowMapper (여기로 통일)
    private static final RowMapper<Place> ROW_MAPPER = (rs, rowNum) -> {
        Place p = new Place();
        p.setId(rs.getLong("id"));
        p.setName(rs.getString("name"));
        p.setCategory(rs.getString("category"));
        p.setAddress(rs.getString("address"));
        p.setLat(rs.getDouble("lat"));
        p.setLng(rs.getDouble("lng"));
        p.setSido(rs.getString("sido"));
        p.setSigungu(rs.getString("sigungu"));

        // 컬럼이 비어있어도 0으로 안전 매핑 (SELECT에서 COALESCE)
        p.setDiscountPercent(rs.getInt("discountpercent"));
        p.setPopularity(rs.getInt("popularity"));
        return p;
    };

    public PlaceRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Place> findByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();

        String sql = """
            SELECT
                id, name, category, address, lat, lng, sido, sigungu,
                COALESCE(discountpercent, 0) AS discountpercent,
                COALESCE(popularity, 0)      AS popularity
            FROM places
            WHERE id IN (:ids)
            """;

        Map<String, Object> params = new HashMap<>();
        params.put("ids", ids);

        return jdbc.query(sql, params, ROW_MAPPER);
    }

    public List<Place> searchByRegion(String sido, String sigungu, int limit, int offset) {
        StringBuilder sql = new StringBuilder("""
            SELECT
                id, name, category, address, lat, lng, sido, sigungu,
                COALESCE(discountpercent, 0) AS discountpercent,
                COALESCE(popularity, 0)      AS popularity
            FROM places
            WHERE 1=1
            """);

        Map<String, Object> p = new HashMap<>();
        if (sido != null && !sido.isBlank()) {
            sql.append(" AND sido = :sido");
            p.put("sido", sido);
        }
        if (sigungu != null && !sigungu.isBlank()) {
            sql.append(" AND sigungu = :sigungu");
            p.put("sigungu", sigungu);
        }
        sql.append(" ORDER BY id DESC LIMIT :limit OFFSET :offset");
        p.put("limit", Math.max(0, limit));
        p.put("offset", Math.max(0, offset));

        return jdbc.query(sql.toString(), p, ROW_MAPPER);
    }
}
