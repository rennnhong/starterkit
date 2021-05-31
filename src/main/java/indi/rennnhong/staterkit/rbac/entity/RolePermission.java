package indi.rennnhong.staterkit.rbac.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
public class RolePermission {

    @ManyToOne
    Permission permission;

    @ManyToOne
    Action action;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RolePermission that = (RolePermission) o;
        return Objects.equals(permission, that.permission) &&
                Objects.equals(action, that.action);
    }

    @Override
    public int hashCode() {
        return Objects.hash(permission, action);
    }

    public RolePermission(Permission permission, Action action) {
        this.permission = permission;
        this.action = action;
    }
}
