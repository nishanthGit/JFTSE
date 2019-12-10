package com.ft.emulator.server.database.model.account;

import com.ft.emulator.common.model.AbstractBaseModel;
import com.ft.emulator.server.database.model.character.CharacterPlayer;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Audited
@Entity
public class Account extends AbstractBaseModel {

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "account")
    private List<CharacterPlayer> characterPlayerList;

    private Integer ap;

    private Date lastLogin;

    @Column(unique = true)
    private String username;

    private String password;

    // char
    private Integer status;

    private Boolean gameMaster;
}