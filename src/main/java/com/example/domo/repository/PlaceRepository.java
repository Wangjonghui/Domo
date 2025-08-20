// src/main/java/com/example/domo/repository/PlaceRepository.java
package com.example.domo.repository;

import com.example.domo.model.Place;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class PlaceRepository {

    private final JdbcTemplate jdbc;

    public PlaceRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** 시/도 + 시군구(선택)로 조회 */
    public List<Place> findByRegion(String sido, String sigungu, int limit, int offset) {
        StringBuilder sql = new StringBuilder("""
        select
            place_id,
            name,
            category,
            address,
            lat,
            lng,
            sido,
            sigungu,
            coalesce(discountpercent, 0) as discountpercent,
            /* popularity 컬럼이 없을 가능성 대비 */
            0 as popularity
        from public.places
        where 1=1
    """);

        List<Object> params = new java.util.ArrayList<>();

        if (sido != null && !sido.isBlank()) {
            sql.append(" and sido = ? ");
            params.add(sido);
        }
        if (sigungu != null && !sigungu.isBlank()) {
            sql.append(" and sigungu = ? ");
            params.add(sigungu);
        }

        sql.append(" order by name limit ? offset ? ");
        params.add(limit);
        params.add(offset);

        return jdbc.query(sql.toString(), params.toArray(), new PlaceRowMapper());
    }

    /** place_id 목록으로 조회 */
    public List<Place> findByIds(List<UUID> placeIds) {
        if (placeIds == null || placeIds.isEmpty()) return Collections.emptyList();
        String placeholders = placeIds.stream().map(id -> "?").collect(Collectors.joining(","));
        String sql = """
            select
                place_id,
                name,
                category,
                address,
                lat,
                lng,
                sido,
                sigungu,
                coalesce(discountpercent, 0) as discountpercent,
                coalesce(popularity, 0)      as popularity
            from public.places
            where place_id in (%s)
        """.formatted(placeholders);
        return jdbc.query(sql, placeIds.toArray(), new PlaceRowMapper());
    }

    /** 이름/주소 키워드 검색(선택 기능) */
    public List<Place> search(String keyword, int limit, int offset) {
        String like = "%" + keyword + "%";
        String sql = """
            select
                place_id,
                name,
                category,
                address,
                lat,
                lng,
                sido,
                sigungu,
                coalesce(discountpercent, 0) as discountpercent,
                coalesce(popularity, 0)      as popularity
            from public.places
            where name ilike ? or address ilike ?
            order by name
            limit ? offset ?
        """;
        return jdbc.query(sql, new Object[]{like, like, limit, offset}, new PlaceRowMapper());
    }

    /** 공통 매핑 */
    private static class PlaceRowMapper implements RowMapper<Place> {
        @Override
        public Place mapRow(ResultSet rs, int rowNum) throws SQLException {
            Place p = new Place();
            p.setName(rs.getString("name"));
            p.setCategory(rs.getString("category"));
            p.setAddress(rs.getString("address"));
            p.setLat(safeDouble(rs, "lat"));
            p.setLng(safeDouble(rs, "lng"));
            p.setSido(rs.getString("sido"));
            p.setSigungu(rs.getString("sigungu"));
            p.setDiscountPercent(rs.getInt("discountpercent"));
            p.setPopularity(rs.getInt("popularity"));
            // 거리/점수는 런타임 계산
            return p;
        }
        private static double safeDouble(ResultSet rs, String col) throws SQLException {
            double v = rs.getDouble(col);
            return rs.wasNull() ? 0.0 : v;
        }
    }
}
