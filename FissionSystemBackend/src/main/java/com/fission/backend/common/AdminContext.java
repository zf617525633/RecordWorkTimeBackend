package com.fission.backend.common;

import com.fission.backend.entity.SysAdmin;

public class AdminContext {
    private static final ThreadLocal<SysAdmin> ADMIN_HOLDER = new ThreadLocal<>();

    public static void set(SysAdmin admin) {
        ADMIN_HOLDER.set(admin);
    }

    public static SysAdmin get() {
        return ADMIN_HOLDER.get();
    }

    public static void remove() {
        ADMIN_HOLDER.remove();
    }
}
