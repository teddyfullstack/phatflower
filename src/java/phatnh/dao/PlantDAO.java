/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phatnh.dao;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.naming.NamingException;
import phatnh.model.Plant;
import phatnh.builder.QueryBuilder;
import phatnh.model.Plants;
import phatnh.util.ErrorHandler;
import phatnh.util.XMLUtil;

/**
 *
 * @author nguyenhongphat0
 */
public class PlantDAO implements Serializable {
    private Plants plants;

    public PlantDAO() {
        plants = new Plants();
    }

    public Plants getPlants() {
        return plants;
    }
    
    public List<Plant> getPlantList() {
        return plants.getPlant();
    }
    
    public String getReadablePrice(Plant plant) {
        DecimalFormat f = new DecimalFormat();
        f.setGroupingUsed(true);
        f.setGroupingSize(3);
        return f.format(plant.getPrice());
    }
    
    public String comparePrice(Plant p1, Plant p2) {
        float diff = p1.getPrice().floatValue() - p2.getPrice().floatValue();
        p1.setPrice(new BigDecimal(Math.abs(diff)));
        String result = getReadablePrice(p1) + " vnđ";
        if (diff > 0) {
            return "Đắt hơn " + result;
        } else if (diff < 0) {
            return "Rẻ hơn " + result;
        } else {
            return "Bằng giá";
        }
    }
    
    public String getDomain(Plant plant) {
        return XMLUtil.getDomainFromURL(plant.getLink());
    }
    
    public void all() {
        try {
            new QueryBuilder()
                    .prepare("SELECT id, name, link, image, price FROM products")
                    .executeQuery()
                    .fetch(new QueryBuilder.ResultSetCallback() {
                        @Override
                        public void forEach(ResultSet res) throws SQLException {
                            Plant plant = new Plant();
                            plant.setId(res.getBigDecimal("id"));
                            plant.setName(res.getString("name"));
                            plant.setLink(res.getString("link"));
                            plant.setImage(res.getString("image"));
                            plant.setPrice(res.getBigDecimal("price"));
                            getPlantList().add(plant);
                        }
                    })
                    .close();
        } catch (NamingException | SQLException ex) {
            ErrorHandler.handle(ex);
        }
    }
    
    public int count() {
        int count = 0;
        try {
            QueryBuilder qb = new QueryBuilder()
                    .prepare("SELECT count(id) FROM products")
                    .executeQuery();
            if (qb.getResultSet().next()) {
                count = qb.getResultSet().getInt(1);
            }
            qb.close();
        } catch (NamingException | SQLException ex) {
            ErrorHandler.handle(ex);
        }
        return count;
    }
    
    public void hot() {
        try {
            new QueryBuilder()
                    .prepare("SELECT id, name, link, image, price FROM products WHERE is_hot = 1")
                    .executeQuery()
                    .fetch(new QueryBuilder.ResultSetCallback() {
                        @Override
                        public void forEach(ResultSet res) throws SQLException {
                            Plant plant = new Plant();
                            plant.setId(res.getBigDecimal("id"));
                            plant.setName(res.getString("name"));
                            plant.setLink(res.getString("link"));
                            plant.setImage(res.getString("image"));
                            plant.setPrice(res.getBigDecimal("price"));
                            getPlantList().add(plant);
                        }
                    })
                    .close();
        } catch (NamingException | SQLException ex) {
            ErrorHandler.handle(ex);
        }
    }
    
    public Plant find(String id) {
        final Plant plant = new Plant();
        try {
            new QueryBuilder()
                    .prepare("SELECT id, name, link, image, price FROM products WHERE id = " + id)
                    .executeQuery()
                    .fetch(new QueryBuilder.ResultSetCallback() {
                        @Override
                        public void forEach(ResultSet res) throws SQLException {
                            plant.setId(res.getBigDecimal("id"));
                            plant.setName(res.getString("name"));
                            plant.setLink(res.getString("link"));
                            plant.setImage(res.getString("image"));
                            plant.setPrice(res.getBigDecimal("price"));
                        }
                    })
                    .close();
        } catch (NamingException | SQLException ex) {
            ErrorHandler.handle(ex);
        }
        return plant;
    }
    
    public void findAll(String ids) { // ids is a tokenized string in this format: 1,2,3
        try {
            new QueryBuilder()
                    .prepare("SELECT id, name, link, image, price FROM products WHERE id IN (" + ids + ")")
                    .executeQuery()
                    .fetch(new QueryBuilder.ResultSetCallback() {
                        @Override
                        public void forEach(ResultSet res) throws SQLException {
                            Plant plant = new Plant();
                            plant.setId(res.getBigDecimal("id"));
                            plant.setName(res.getString("name"));
                            plant.setLink(res.getString("link"));
                            plant.setImage(res.getString("image"));
                            plant.setPrice(res.getBigDecimal("price"));
                            getPlantList().add(plant);
                        }
                    })
                    .close();
        } catch (NamingException | SQLException ex) {
            ErrorHandler.handle(ex);
        }
    }
    
    public void search(String name, String domain, String limit) {
        try {
            new QueryBuilder()
                    .prepare("SELECT id, name, link, image, price FROM products"
                            + " WHERE name LIKE ?"
                            + " AND link LIKE ?"
                            + " LIMIT " + limit)
                    .setString(1, "%" + name + "%")
                    .setString(2, "%" + domain + "%")
                    .executeQuery()
                    .fetch(new QueryBuilder.ResultSetCallback() {
                        @Override
                        public void forEach(ResultSet res) throws SQLException {
                            Plant plant = new Plant();
                            plant.setId(res.getBigDecimal("id"));
                            plant.setName(res.getString("name"));
                            plant.setLink(res.getString("link"));
                            plant.setImage(res.getString("image"));
                            plant.setPrice(res.getBigDecimal("price"));
                            getPlantList().add(plant);
                        }
                    })
                    .close();
        } catch (NamingException | SQLException ex) {
            ErrorHandler.handle(ex);
        }
    }
    
    public boolean insert(Plant dto) {
        try {
            return new QueryBuilder()
                    .prepare("INSERT INTO products(name, link, image, price) VALUES (?, ?, ?, ?)")
                    .setString(1, dto.getName())
                    .setString(2, dto.getLink())
                    .setString(3, dto.getImage())
                    .setBigDecimal(4, dto.getPrice())
                    .executeUpdate()
                    .close()
                    .isSuccess();
        } catch (NamingException | SQLException e) {
            ErrorHandler.handle(e);
            return false;
        }
    }
    
    public void warming() {
        try {
            new QueryBuilder()
                    .prepare("UPDATE products SET is_hot = 0 WHERE is_hot = 1")
                    .executeUpdate()
                    .close();
        } catch (NamingException | SQLException e) {
            ErrorHandler.handle(e);
        }
    }
    
    public int insertAll() {
        int count = 0;
        try {
            QueryBuilder qb = new QueryBuilder();
            for (Plant plant : getPlantList()) {
                boolean exist = false;
                qb.prepare("SELECT link FROM products WHERE link = ?")
                        .setString(1, plant.getLink())
                        .executeQuery();
                if (qb.getResultSet().next()) {
                    exist = true;
                }
                if (!exist && plant.getPrice().compareTo(new BigDecimal(0)) == 1) {
                    qb.prepare("INSERT INTO products(name, link, image, price) VALUES (?, ?, ?, ?)")
                            .setString(1, plant.getName())
                            .setString(2, plant.getLink())
                            .setString(3, plant.getImage())
                            .setBigDecimal(4, plant.getPrice())
                            .executeUpdate();
                    count++;
                }
            }
            qb.close();
        } catch (NamingException | SQLException e) {
            ErrorHandler.handle(e);
        }
        return count;
    }
    
    public boolean insertContent(String id, String content) {
        try {
            int idValue = Integer.parseInt(id);
            return new QueryBuilder()
                    .prepare("INSERT INTO product_contents(id, content) VALUES (?, ?)")
                    .setInt(1, idValue)
                    .setString(2, content)
                    .executeUpdate()
                    .close()
                    .isSuccess();
        } catch (NumberFormatException | NamingException | SQLException e) {
            ErrorHandler.handle(e);
            return false;
        }
    }
    
    public String fetchContent(String id) {
        final StringBuilder sb = new StringBuilder();
        try {
            int idValue = Integer.parseInt(id);
            new QueryBuilder()
                    .prepare("SELECT content FROM product_contents WHERE id = ?")
                    .setInt(1, idValue)
                    .executeQuery()
                    .fetch(new QueryBuilder.ResultSetCallback() {
                        @Override
                        public void forEach(ResultSet res) throws SQLException {
                            sb.append(res.getString(1));
                        }
                    })
                    .close();
        } catch (NumberFormatException | NamingException | SQLException e) {
            ErrorHandler.handle(e);
        }
        return sb.toString();
    }
    
    public List<String> fetchNames() throws NamingException, SQLException {
        final List<String> names = new ArrayList<>();
        new QueryBuilder()
                .prepare("SELECT name FROM products")
                .executeQuery()
                .fetch(new QueryBuilder.ResultSetCallback() {
                    @Override
                    public void forEach(ResultSet res) throws SQLException {
                        names.add(res.getString("name").toLowerCase());
                    }
                });
        return names;
    }
    
    public void findSimilar(String name) {
        try {
            String sql = "SELECT id, name, link, image, price FROM products "
                    + "WHERE POSITION("
                        + "(SELECT name FROM categories "
                        + "WHERE COUNT <= 10 AND POSITION(name IN ?) > 0 "
                        + "ORDER BY COUNT DESC LIMIT 1) "
                        + "IN "
                        + "name"
                    + ") > 0";
            new QueryBuilder()
                    .prepare(sql)
                    .setString(1, name)
                    .executeQuery()
                    .fetch(new QueryBuilder.ResultSetCallback() {
                        @Override
                        public void forEach(ResultSet res) throws SQLException {
                            Plant plant = new Plant();
                            plant.setId(res.getBigDecimal("id"));
                            plant.setName(res.getString("name"));
                            plant.setLink(res.getString("link"));
                            plant.setImage(res.getString("image"));
                            plant.setPrice(res.getBigDecimal("price"));
                            getPlantList().add(plant);
                        }
                    })
                    .close();
        } catch (NamingException | SQLException ex) {
            ErrorHandler.handle(ex);
        }
    }
    
    public void mostViewedPlants(String limit) {
        try {
            String sql = "SELECT count(a.id) as 'views', p.* FROM analytics a, products p "
                    + "WHERE a.product IS NOT NULL and  a.product = p.id "
                    + "GROUP BY a.product "
                    + "ORDER BY views DESC "
                    + "LIMIT " + limit;
            new QueryBuilder()
                    .prepare(sql)
                    .executeQuery()
                    .fetch(new QueryBuilder.ResultSetCallback() {
                        @Override
                        public void forEach(ResultSet res) throws SQLException {
                            Plant plant = new Plant();
                            plant.setId(res.getBigDecimal("id"));
                            plant.setName(res.getString("name"));
                            plant.setLink(res.getString("link"));
                            plant.setImage(res.getString("image"));
                            plant.setPrice(res.getBigDecimal("price"));
                            getPlantList().add(plant);
                        }
                    })
                    .close();
        } catch (NamingException | SQLException ex) {
            ErrorHandler.handle(ex);
        }
    }
}
