package com.hangloose.ui.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.model.LatLngBounds
import com.hangloose.R
import kotlinx.android.synthetic.main.location_search_adapter.view.*
import java.util.*
import java.util.concurrent.TimeUnit
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.PlacesClient
import kotlin.collections.ArrayList


/**
 * Note that this adapter requires a valid [GoogleApiClient].
 * The API client must be maintained in the encapsulating Activity, including all lifecycle and
 * connection states. The API client must be connected with the [Places.GEO_DATA_API] API.
 */
class PlacesAutoCompleteAdapter(
    private val mContext: Context, private val mGoogleApiClient: GoogleApiClient,
    private var mBounds: LatLngBounds?
) : RecyclerView.Adapter<PlacesAutoCompleteAdapter.PredictionHolder>(), Filterable {
    private var mResultList: ArrayList<PlaceAutocomplete>? = ArrayList()
    private var placesClient: PlacesClient? = null


    /**
     * Sets the bounds for all subsequent queries.
     */
    fun setBounds(bounds: LatLngBounds) {
        mBounds = bounds
    }

    /**
     * Returns the filter for the current set of autocomplete results.
     */
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                // Skip the autocomplete query if no constraints are given.
                if (constraint != null) {
                    // Query the autocomplete API for the (constraint) search string.
                    mResultList = getAutocomplete(constraint)
                    Log.d("AutoComplete", "ResultList : " + mResultList)
                    if (mResultList != null) {
                        // The API successfully returned results.
                        results.values = mResultList
                        results.count = mResultList!!.size
                    }
                }
                return results
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults?) =
                if (results != null && results.count > 0) {
                    Log.d("AutoComplete", "publishResults : " + results.values)
                    notifyDataSetChanged()
                } else {
                    Log.d("AutoComplete", "publishResults : " + results!!.values)
                    notifyDataSetChanged()
                }
        }
    }

    private fun getAutocomplete(constraint: CharSequence?): ArrayList<PlaceAutocomplete>? {

        if (!com.google.android.libraries.places.api.Places.isInitialized()) {
            com.google.android.libraries.places.api.Places.initialize(mContext, "AIzaSyAtN-pysxdUadTT-r4Y1sfC6maTvbDTGfs")
        }

        // Create a new Places client instance.
        placesClient = com.google.android.libraries.places.api.Places.createClient(mContext)

        Toast.makeText(
            mContext,
            constraint.toString(),
            Toast.LENGTH_SHORT
        ).show()
        // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
        // and once again when the user makes a selection (for example when calling fetchPlace()).
        val token = AutocompleteSessionToken.newInstance()
        // Create a RectangularBounds object.
        val bounds = RectangularBounds.newInstance(
            LatLng(-33.880490, 151.184363), //dummy lat/lng
            LatLng(-33.858754, 151.229596)
        )
        // Use the builder to create a FindAutocompletePredictionsRequest.
        val request = FindAutocompletePredictionsRequest.builder()
            // Call either setLocationBias() OR setLocationRestriction().\
            //.setLocationRestriction(bounds)
            .setCountry("in")
            .setTypeFilter(TypeFilter.ADDRESS)
            .setSessionToken(token)
            .setQuery(constraint.toString())
            .build()

        var resultList:ArrayList<PlaceAutocomplete>? = ArrayList()

        placesClient!!.findAutocompletePredictions(request).addOnSuccessListener {
            val autocompletePredictions = it.autocompletePredictions
//            for (prediction in autocompletePredictions) {
//
//            }

            val iterator = autocompletePredictions.iterator()
            Log.d("PlaceAuto" , "Count : " + autocompletePredictions.count())
            resultList = ArrayList<PlaceAutocomplete>(autocompletePredictions.count())
            while (iterator.hasNext()) {
                val prediction = iterator.next()
                // Get the details of this prediction and copy it into a new PlaceAutocomplete object.
                resultList!!.add(
                    PlaceAutocomplete(
                        prediction.placeId,
                        prediction.getFullText(null)
                    )
                )
                mResultList!!.addAll(resultList!!)
                Log.d("AutoComplete", "iterator : " + resultList)
            }
        }.addOnFailureListener {
            if (it is ApiException) {
                    var apiException =  it
                    Log.e("PlaceAutoComplete", "Place not found: " + apiException.getStatusCode());
                }
        }
        Log.d("AutoComplete", "Result : " + mResultList)
        return mResultList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PredictionHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.location_search_adapter, parent, false)
        Log.i("Place", "onCreateViewHolder")
        return PredictionHolder(view)
    }

    override fun getItemCount(): Int {
        return if (mResultList != null)
            mResultList!!.size
        else
            0
    }

    fun getItem(position: Int): PlaceAutocomplete {
        return mResultList!![position]
    }

    override fun onBindViewHolder(holder: PredictionHolder, position: Int) {
        Log.i("Place", "onBindViewHolder : " + mResultList!![position])
        holder.bindActivitiesView(mResultList!![position])
    }

    inner class PredictionHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindActivitiesView(contentItem: PlaceAutocomplete) {
            itemView.tvAddress.text = contentItem.description
        }
    }

    /**
     * Holder for Places Geo Data Autocomplete API results.
     */
    inner class PlaceAutocomplete internal constructor(var placeId: CharSequence?, var description: CharSequence?) {

        override fun toString(): String {
            return description.toString()
        }
    }

    companion object {

        private const val TAG = "PlacesAutoCompleteAdapter"
    }
}