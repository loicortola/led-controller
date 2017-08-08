package com.loicortola.controller.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;

import com.loicortola.controller.device.DeviceTypeResolverService;
import com.loicortola.controller.persistence.DaoMaster;
import com.loicortola.controller.persistence.DaoSession;
import com.loicortola.controller.persistence.DeviceDao;
import com.loicortola.controller.persistence.DeviceMapper;
import com.loicortola.controller.model.Device;
import com.loicortola.controller.resolver.MockedResolver;
import com.loicortola.controller.resolver.MulticastDNSResolver;
import com.loicortola.controller.resolver.SSDPResolver;

import java.util.List;

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
    // Different kinds of resolvers
    private SSDPResolver mSsdpResolver;
    private MulticastDNSResolver mDNSResolver;
    private MockedResolver mMockedResolver;
    private DeviceTypeResolverService deviceTypeResolverService;

    private static final String TAG = DeviceService.class.getSimpleName();

    public interface OnDeviceResolvedListener {
        void onDeviceResolved(Device d);
        void onStopped();
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

    public void remove(String deviceId) {
        Device device = get(deviceId);
        deviceDao.deleteByKey(device.getDbId());
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
        if (mSsdpResolver != null) {
            mSsdpResolver.stop();
        }
        if (mMockedResolver != null) {
            mMockedResolver.stop();
        }


        OnDeviceResolvedListener listener = new OnDeviceResolvedListener() {

            @Override
            public void onDeviceResolved(Device newDevice) {
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

            @Override
            public void onStopped() {
                mDNSResolver = null;
                mSsdpResolver = null;
                mMockedResolver = null;
            }
        };

        mDNSResolver = new MulticastDNSResolver(ctx, mHandler, deviceTypeResolverService, "_http._tcp.", listener);
        mSsdpResolver = new SSDPResolver(ctx, mHandler, deviceTypeResolverService, listener);
        mMockedResolver = new MockedResolver(deviceTypeResolverService, listener);

        mDNSResolver.run();
        mSsdpResolver.run();
        mMockedResolver.run();

    }

}
