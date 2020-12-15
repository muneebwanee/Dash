package com.github.muneebwanee.dash.ui.fragments.maps

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import cn.pedant.SweetAlert.SweetAlertDialog
import com.github.clans.fab.FloatingActionButton
import com.github.muneebwanee.dash.R
import com.github.muneebwanee.dash.data.model.Location
import com.github.muneebwanee.dash.ui.fragments.base.BaseFragment
import com.github.muneebwanee.dash.ui.widget.toolbar.CustomToolbar
import com.github.muneebwanee.dash.utils.ConstFun.isScrollToolbar
import com.github.muneebwanee.dash.utils.ConstFun.sendToGoogleMaps
import com.github.muneebwanee.dash.utils.ConstFun.setPaddingCompass
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.github.muneebwanee.dash.data.preference.DataSharePreference.statePersmissionLocationShow
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.database.DataSnapshot
import com.jakewharton.rxbinding2.view.RxView
import com.pawegio.kandroid.e
import kotterknife.bindView
import javax.inject.Inject

/**
 * Created by muneebwanee on 15/12/20.
 */
class MapsFragment : BaseFragment(R.layout.fragment_maps), OnMapReadyCallback, InterfaceViewMaps {

    companion object { const val TAG = "MapsFragment" }

    private val toolbar : CustomToolbar by bindView(R.id.toolbar)
    private val btnLocation : FloatingActionButton by bindView(R.id.floating_button_location)
    private val btnExport : FloatingActionButton by bindView(R.id.floating_button_export)
    private val appBar : AppBarLayout by bindView(R.id.app_bar)
    private val main : CoordinatorLayout by bindView(R.id.main_view)

    private var latitude : Double ?= null
    private var longitude : Double ?= null
    private var maps: GoogleMap?=null

    @Inject
    lateinit var mapsFragment: SupportMapFragment

    @Inject
    lateinit var interactor: InterfaceInteractorMaps<InterfaceViewMaps>

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setToolbar(toolbar,false,R.string.maps,0)
        isScrollToolbar(toolbar, false)
        if (getComponent() != null) {
            getComponent()!!.inject(this)
            interactor.onAttach(this)
            initializeMaps()
            onClick()
        }
    }

    @SuppressLint("CheckResult")
    private fun onClick() {
        RxView.clicks(btnLocation).subscribe({
            if (latitude !=null && longitude!=null && maps!=null)
                maps!!.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude!!, longitude!!), 16f))
        },{e(TAG,it.message.toString())})
        RxView.clicks(btnExport).subscribe({
            if (latitude !=null && longitude!=null && maps!=null)
                context!!.sendToGoogleMaps(latitude!!.toString(),longitude!!.toString())
        },{e(TAG,it.message.toString())})
    }

    override fun onStart() {
        super.onStart()
        interactor.valueEventEnableGps()
        interactor.valueEventEnablePermission()
    }

    private fun initializeMaps() {
        val transaction = childFragmentManager.beginTransaction()
        transaction.add(R.id.maps_fragment, mapsFragment).commit()
        mapsFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        this.maps = map
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(5.3726154, -73.9312649), 5f))
        mapsFragment.view.setPaddingCompass(appBar)
        map.uiSettings.isMapToolbarEnabled = false
        map.setOnMarkerClickListener { btnExport.show(true) ; return@setOnMarkerClickListener false }
        map.setOnInfoWindowClickListener { btnExport.show(true) }
        map.setOnMapClickListener { btnExport.hide(true) }
        interactor.valueEventLocation()
    }

    override fun setLocation(location: Location) {
        maps?.clear()
        latitude = location.latitude!!
        longitude = location.longitude!!
        val address = location.address
        val dateTime = location.dateTime
        maps?.addMarker(MarkerOptions().position(LatLng(latitude!!, longitude!!)).title(address).snippet(dateTime)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location)))?.showInfoWindow()
        maps?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude!!, longitude!!), 16f))
    }

    override fun setValueState(dataSnapshot: DataSnapshot) {
        toolbar.enableStatePermission = true
        try {
            if (dataSnapshot.exists()) toolbar.statePermission = dataSnapshot.value as Boolean
            else toolbar.statePermission = false
        } catch (t: Throwable) {
            e(TAG, t.message.toString())
        }
    }

    override fun setValuePermission(dataSnapshot: DataSnapshot) {
        try {
            if (dataSnapshot.exists()) {
                if (dataSnapshot.value as Boolean) {
                    context!!.statePersmissionLocationShow = true
                } else {
                    toolbar.showProgress = false
                    if (context!!.statePersmissionLocationShow)
                        showDialog(SweetAlertDialog.ERROR_TYPE, R.string.ops, getString(R.string.message_dialog_permission_location_disable), android.R.string.ok) {
                            setConfirmClickListener {
                                context.statePersmissionLocationShow = false
                                hideDialog()
                            }
                            show()
                        }
                }
            }
        } catch (t: Throwable) {
            e(TAG, t.message.toString())
        }
    }

    override fun onButtonClicked(buttonCode: Int) {
        when(buttonCode){
            CustomToolbar.BUTTON_CHILD_USER -> changeChild(TAG)
            CustomToolbar.BUTTON_STATE -> showSnackbar(if (toolbar.statePermission) R.string.enabled_gps else R.string.disabled_gps,main)
            else -> super.onButtonClicked(buttonCode)
        }
    }

    override fun onDetach() {
        interactor.onDetach()
        super.onDetach()
    }


}