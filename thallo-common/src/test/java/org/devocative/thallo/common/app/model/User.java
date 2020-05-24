package org.devocative.thallo.common.app.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;

import static org.devocative.thallo.common.app.model.User.UC_OA_USER_USERNAME;

@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@Entity
@Table(name = "t_user", uniqueConstraints = {
	@UniqueConstraint(name = UC_OA_USER_USERNAME, columnNames = {"c_username"})
})
public class User {
	public static final String UC_OA_USER_USERNAME = "uc_oaUser_username_index";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "c_username")
	private String username;

}
