package org.devocative.thallo.common.app;

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
@Table(name = "t_user_log")

public class UserLog {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "c_desc")
	private String description;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "f_user", nullable = false, insertable = false, updatable = false,
		foreignKey = @ForeignKey(name = "fk_userLog2user"))
	private User user;

	@Column(name = "f_user", nullable = false)
	private Long userId;
}
