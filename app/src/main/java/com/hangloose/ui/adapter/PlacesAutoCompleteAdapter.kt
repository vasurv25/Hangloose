package com.hangloose.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
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

/**
 * Note that this adapter requires a valid [GoogleApiClient].
 * The API client must be maintained in the encapsulating Activity, including all lifecycle and
 * connection states. The API client must be connected with the [Places.GEO_DATA_API] API.
 */
class PlacesAutoCompleteAdapter(
    private val mContext: Context, private val mGoogleApiClient: GoogleApiClient,
    private var mBounds: LatLngBounds?
) : RecyclerView.Adapter<PlacesAutoCompleteAdapter.PredictionHolder>(), Filterable {
    private var mResultList: ArrayList<PlaceAutocomplete>? = null

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
                    notifyDataSetChanged()
                } else {
                    notifyDataSetChanged()
                }
        }
    }

    private fun getAutocomplete(constraint: CharSequence?): ArrayList<PlaceAutocomplete>? {
        if (mGoogleApiClient.isConnected) {
            Log.i("", "Starting autocomplete query for: " + constraint!!)

            // Submit the query to the autocomplete API and retrieve a PendingResult that will
            // contain the results when the query completes.
            val results = Places.GeoDataApi
                .getAutocompletePredictions(
                    mGoogleApiClient, constraint.toString(),
                    mBounds, null
                )

            // This method should have been called off the main UI thread. Block and wait for at most 60s
            // for a result from the API.
            val autocompletePredictions = results
                .await(60, TimeUnit.SECONDS)

            // Confirm that the query completed successfully, otherwise return null
            val status = autocompletePredictions.status
            if (!status.isSuccess) {
                Toast.makeText(
                    mContext, "${status.statusMessage}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("", "Error getting autocomplete prediction API call: $status")
                autocompletePredictions.release()
                return null
            }

            Log.i(
                "", "Query completed. Received " + autocompletePredictions.count
                        + " predictions."
            )

            // Copy the results into our own data structure, because we can't hold onto the buffer.
            // AutocompletePrediction objects encapsulate the API response (place ID and description).

            val iterator = autocompletePredictions.iterator()
            Log.d("PlaceAuto" , "Count : " + autocompletePredictions.count)
            val resultList = ArrayList<PlaceAutocomplete>(autocompletePredictions.count)
            while (iterator.hasNext()) {
                val prediction = iterator.next()
                // Get the details of this prediction and copy it into a new PlaceAutocomplete object.
                resultList.add(
                    PlaceAutocomplete(
                        prediction.placeId,
                        prediction.getFullText(null)
                    )
                )
            }

            // Release the buffer now that all data has been copied.
            autocompletePredictions.release()

            return resultList
        }
        Log.e("", "Google API client is not connected for autocomplete query.")
        return null
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): PredictionHolder {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.location_search_adapter, parent, false)
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

    override fun onBindViewHolder(holder: PredictionHolder?, position: Int) {
        Log.i("Place", "onBindViewHolder")
        holder!!.bindActivitiesView(mResultList!![position])
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