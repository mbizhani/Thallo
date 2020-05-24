package org.devocative.thallo.test.app.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;

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

}
