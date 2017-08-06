package com.loicortola.controller.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.nsd.NsdServiceInfo;
import android.os.Handler;
import android.util.Log;

import com.loicortola.controller.persistence.DaoMaster;
import com.loicortola.controller.persistence.DaoSession;
import com.loicortola.controller.persistence.DeviceDao;
import com.loicortola.controller.persistence.DeviceMapper;
import com.loicortola.controller.model.Device;
import com.loicortola.controller.resolver.DeviceTypeResolver;
import com.loicortola.controller.resolver.MulticastDNSResolver;
import com.loicortola.controller.resolver.SSDPResolver;

import java.util.List;

import io.resourcepool.jarpic.model.SsdpService;

/**
 * Created by loic on 28/03/2016.
 */
public class DeviceService {

    private Handler mHandler;
    private Context ctx;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private DeviceDao deviceDao;
    private DeviceMapper deviceMapper;
    private SSDPResolver ssdpResolver;
    private MulticastDNSResolver mDNSResolver;
    private DeviceTypeResolverService deviceTypeResolverService;

    private static final String TAG = DeviceService.class.getSimpleName();

    public void remove(String deviceId) {
        Device device = get(deviceId);
        deviceDao.deleteByKey(device.getDbId());
    }

    public interface OnDeviceResolvedListener {
        void onDeviceResolved(Device d);
    }


    public DeviceService(Context ctx) {
        this.ctx = ctx.getApplicationContext();

        mHandler = new Handler();

        deviceTypeResolverService = new DeviceTypeResolverService();
        deviceMapper = new DeviceMapper(deviceTypeResolverService);

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(ctx.getApplicationContext(), "device-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        deviceDao = daoSession.getDeviceDao();
    }

    public boolean exists(String deviceId) {
        return deviceDao.queryBuilder()
                .where(DeviceDao.Properties.DeviceId.eq(deviceId))
                .count() > 0;
    }

    public void save(Device d) {
        com.loicortola.controller.persistence.Device newDeviceDb = deviceMapper.map(d);
        if (newDeviceDb.getId() == null) {
            Device oldDevice = get(d.getId());
            if (oldDevice == null) {
                // Add
                deviceDao.insert(newDeviceDb);
                d.setDbId(newDeviceDb.getId());
                return;
            } else {
                newDeviceDb.setId(oldDevice.getDbId());
            }
        }
        // Update
        deviceDao.update(newDeviceDb);
    }

    public Device get(String id) {
        return deviceMapper.map(deviceDao.queryBuilder()
                .where(DeviceDao.Properties.DeviceId.eq(id))
                .build()
                .unique());
    }

    public List<Device> getAll() {
        return deviceMapper.mapFromEntity(deviceDao.queryBuilder().list());
    }

    public void refresh(final OnDeviceResolvedListener l) {
        if (mDNSResolver != null) {
            mDNSResolver.stop();
        }

        if (ssdpResolver != null) {
            ssdpResolver.stop();
        }

        mDNSResolver = new MulticastDNSResolver(ctx, mHandler, "_http._tcp.", new MulticastDNSResolver.OnServiceResolvedListener() {
            @Override
            public void onServiceResolved(NsdServiceInfo info) {
                DeviceTypeResolver resolver = deviceTypeResolverService.get(info);
                if (resolver != null) {
                    Device newDevice = resolver.resolve(info);
                    Log.d(TAG, "Service discovery success for device " + newDevice);
                    if (!exists(newDevice.getId())) {
                        // Save or update device
                        save(newDevice);
                    }
                    // Callback
                    if (l != null) {
                        l.onDeviceResolved(newDevice);
                    }
                }
            }

            @Override
            public void onStopped() {
                mDNSResolver = null;
            }
        });
        mDNSResolver.run();

        ssdpResolver = new SSDPResolver(ctx, mHandler, new SSDPResolver.OnServiceResolvedListener() {
            @Override
            public void onServiceResolved(SsdpService service) {
                DeviceTypeResolver resolver = deviceTypeResolverService.get(service);
                if (resolver != null) {
                    Device newDevice = resolver.resolve(service);
                    Log.d(TAG, "Service discovery success for device " + newDevice);
                    if (!exists(newDevice.getId())) {
                        // Save or update device
                        save(newDevice);
                    }
                    // Callback
                    if (l != null) {
                        l.onDeviceResolved(newDevice);
                    }
                }
            }

            @Override
            public void onStopped() {
                ssdpResolver = null;
            }
        });
        ssdpResolver.run();
    }

}
