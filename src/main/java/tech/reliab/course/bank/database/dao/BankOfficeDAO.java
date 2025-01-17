package tech.reliab.course.bank.database.dao;

import tech.reliab.course.bank.database.config.DataSource;
import tech.reliab.course.bank.entity.Bank;
import tech.reliab.course.bank.entity.BankOffice;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BankOfficeDAO implements DAO<BankOffice, Long> {
    public static BankOffice builderBankOffice(ResultSet rs) throws SQLException {
        return BankOffice.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .address(rs.getString("address"))
                .status(rs.getBoolean("status"))
                .freePlaceForAtm(rs.getBoolean("free_place_for_atm"))
                .numberAtms(rs.getInt("number_atms"))
                .creditServices(rs.getBoolean("credit_services"))
                .issuesMoney(rs.getBoolean("issues_money"))
                .depositMoney(rs.getBoolean("deposit_money"))
                .totalMoney(rs.getInt("total_money"))
                .rent(rs.getInt("rent"))
                .bankId(rs.getLong("bank_id"))
                .build();
    }

    @Override
    public BankOffice get(Long id) {
        BankOffice office = new BankOffice();

        String sql = "SELECT * FROM bank_office where id=?";
        try (Connection conn = DataSource.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();
            if (!rs.next()) {
                throw new NullPointerException("Exception: rs is null!");
            }

            office = builderBankOffice(rs);
            rs.close();
            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return office;
    }

    @Override
    public List<BankOffice> getAll() {
        String sql = "SELECT * FROM bank_office";
        List<BankOffice> offices = new ArrayList<>();
        try (Connection conn = DataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                BankOffice office = builderBankOffice(rs);
                offices.add(office);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return offices;
    }

    @Override
    public void insert(BankOffice bankOffice) {
        String sql = "INSERT INTO bank_office (name, address, status, free_place_for_atm, number_atms, " +
                "credit_services, issues_money, deposit_money, total_money, rent, bank_id) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?)";

        try (Connection conn = DataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            Long bankId = bankOffice.getBankId();

            statement.setString(1, bankOffice.getName());
            statement.setString(2, bankOffice.getAddress());
            statement.setBoolean(3, bankOffice.getStatus());
            statement.setBoolean(4, bankOffice.getFreePlaceForAtm());
            statement.setBoolean(6, bankOffice.getCreditServices());
            statement.setBoolean(7, bankOffice.getIssuesMoney());
            statement.setBoolean(8, bankOffice.getDepositMoney());
            statement.setInt(10, bankOffice.getRent());
            statement.setLong(11, bankId);

            // получение кол-ва банкоматов в банке
            String sql2 = "SELECT number_atms FROM bank WHERE bank.id=?";
            PreparedStatement ps2 = conn.prepareStatement(sql2);
            ps2.setLong(1, bankId);
            ResultSet rs2 = ps2.executeQuery();
            if (rs2.next()) {
                statement.setInt(5, rs2.getInt("number_atms"));
            }

            // получение общего кол-ва денег в банке для заполнения этой же переменной в банкомате
            String sql3 = "SELECT total_money FROM bank WHERE bank.id=?";
            PreparedStatement ps3 = conn.prepareStatement(sql3);
            ps3.setLong(1, bankId);
            ResultSet rs3 = ps3.executeQuery();
            if (rs3.next()) {
                statement.setInt(9, rs3.getInt("total_money"));
            }

            statement.executeUpdate();

            // обновление кол-ва офисов в банке
            String sql4 = "UPDATE bank SET number_offices = number_offices + 1 WHERE bank.id=?";
            PreparedStatement ps4 = conn.prepareStatement(sql4);
            ps4.setLong(1, bankId);
            ps4.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void update(BankOffice bankOffice) {
        String updateQuery = "UPDATE bank_office SET" +
                " name=?, address=?, status=?, free_place_for_atm=?, number_atms=?," +
                " credit_services=?, issues_money=?, deposit_money=?, total_money=?, rent=?, bank_id=? " +
                "WHERE bank_office.id=?";

        try (Connection conn = DataSource.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(updateQuery);
            statement.setString(1, bankOffice.getName());
            statement.setString(2, bankOffice.getAddress());
            statement.setBoolean(3, bankOffice.getStatus());
            statement.setBoolean(4, bankOffice.getFreePlaceForAtm());
            statement.setInt(5, bankOffice.getNumberAtms());
            statement.setBoolean(6, bankOffice.getCreditServices());
            statement.setBoolean(7, bankOffice.getIssuesMoney());
            statement.setBoolean(8, bankOffice.getDepositMoney());
            statement.setInt(9, bankOffice.getTotalMoney());
            statement.setInt(10, bankOffice.getRent());
            statement.setLong(11, bankOffice.getBankId());
            statement.setLong(12, bankOffice.getId());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /*
     * Should delete the object found by id in database bank_office.
     * @param id    id-primary key of entity bank_office
     */
    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM bank_office where bank_office.id =?";

        try {
            Connection conn = DataSource.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteAll() {
        String sql = "DELETE FROM bank_office; ALTER SEQUENCE bank_office_id_seq RESTART WITH 1";

        try {
            Connection conn = DataSource.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
