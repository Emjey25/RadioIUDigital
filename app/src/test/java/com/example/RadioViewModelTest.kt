package com.example

import com.example.data.RadioRepository
import com.example.data.StationCategory
import com.example.viewmodel.RadioViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class RadioViewModelTest {

    private lateinit var viewModel: RadioViewModel

    @Before
    fun setUp() {
        viewModel = RadioViewModel()
    }

    @Test
    fun initialState_hasDefaultActiveStationAndPlaying() {
        val state = viewModel.uiState.value
        assertEquals("iu_stereo", state.activeStation.id)
        assertTrue(state.isPlaying)
        assertFalse(state.isMuted)
        assertEquals(4, state.stations.size)
    }

    @Test
    fun togglePlayPause_changesPlayingState() {
        viewModel.togglePlayPause()
        assertFalse(viewModel.uiState.value.isPlaying)

        viewModel.togglePlayPause()
        assertTrue(viewModel.uiState.value.isPlaying)
    }

    @Test
    fun selectStation_updatesActiveStation() {
        val techStation = RadioRepository.initialStations[1]
        viewModel.selectStation(techStation)

        val state = viewModel.uiState.value
        assertEquals("facultad_tec", state.activeStation.id)
        assertTrue(state.isPlaying)
    }

    @Test
    fun toggleFavorite_addsAndRemoves() {
        val stationId = "facultad_tec"
        assertFalse(viewModel.uiState.value.favoriteStationIds.contains(stationId))

        viewModel.toggleFavorite(stationId)
        assertTrue(viewModel.uiState.value.favoriteStationIds.contains(stationId))

        viewModel.toggleFavorite(stationId)
        assertFalse(viewModel.uiState.value.favoriteStationIds.contains(stationId))
    }

    @Test
    fun setCategoryFilter_updatesSelectedCategory() {
        viewModel.setCategoryFilter(StationCategory.TECH)
        assertEquals(StationCategory.TECH, viewModel.uiState.value.selectedCategory)
    }

    @Test
    fun formatElapsedTime_formatsCorrectly() {
        assertEquals("42:18", viewModel.formatElapsedTime(42 * 60 + 18))
        assertEquals("00:05", viewModel.formatElapsedTime(5))
        assertEquals("01:30", viewModel.formatElapsedTime(90))
    }
}
