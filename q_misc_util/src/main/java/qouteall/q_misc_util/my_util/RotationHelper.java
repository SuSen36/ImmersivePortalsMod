package qouteall.q_misc_util.my_util;

import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.world.phys.Vec3;

public class RotationHelper {
    public static boolean isClose(Quaternion a, Quaternion b, float valve) {
        a.normalize();
        b.normalize();
        if (a.r() * b.r() < 0) {
            a.mul(-1);
        }
        float da = a.i() - b.i();
        float db = a.j() - b.j();
        float dc = a.k() - b.k();
        float dd = a.r() - b.r();
        return da * da + db * db + dc * dc + dd * dd < valve;
    }
    
    public static Vec3 getRotated(Quaternion rotation, Vec3 vec) {
        Vector3f vector3f = new Vector3f(vec);
        vector3f.transform(rotation);
        return new Vec3(vector3f);
    }

}
