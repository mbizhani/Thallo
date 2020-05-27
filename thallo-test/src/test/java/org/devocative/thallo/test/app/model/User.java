package org.devocative.thallo.test.app.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@Entity
@Table(name = "t_user", uniqueConstraints = {
	@UniqueConstraint(name = User.UC_OA_USER_USERNAME, columnNames = {"c_username"})
})
public class User {
	public static final String UC_OA_USER_USERNAME = "uc_oaUser_username_index";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "c_username")
	private String username;

	// --------------- NUMBER

	@Column(name = "n_byte")
	private Byte byteValue;

	@Column(name = "n_int")
	private Integer intValue;

	@Column(name = "n_float")
	private Float floatValue;

//	@Column(name = "n_double")
//	private Double doubleValue;

	@Column(name = "n_big_decimal")
	private BigDecimal bigDecimal;

	// --------------- DATE

	@Column(name = "d_simple_date")
	private Date simpleDate;

	@Temporal(TemporalType.DATE)
	@Column(name = "d_date")
	private Date date;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "d_timestamp")
	private Date timestamp;

	@Temporal(TemporalType.TIME)
	@Column(name = "d_time")
	private Date time;

	// --------------- LOB

	@Lob
	@Column(name = "c_lob")
	private String clob;

	@Lob
	@Column(name = "b_lob")
	private byte[] bytes;

}
