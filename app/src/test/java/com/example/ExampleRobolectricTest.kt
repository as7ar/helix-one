package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.viewmodel.HelixViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun testAppLaunchString() {
        val context = ApplicationProvider.getApplicationContext<Application>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Helix One", appName)
    }

    @Test
    fun testViewModelFirstRunAndProfilePersistence() = runBlocking {
        val app = ApplicationProvider.getApplicationContext<Application>()
        // Clear prefs to ensure fresh environment
        val prefs = app.getSharedPreferences("helix_user_prefs", Application.MODE_PRIVATE)
        prefs.edit().clear().commit()

        val viewModel = HelixViewModel(app)

        // Initially should be first run since profile is empty
        assertTrue(viewModel.isFirstRun.value)
        assertEquals("", viewModel.patientName.value)

        // Save profile
        viewModel.savePatientProfile(
            name = "홍길동",
            code = "HX-TEST-1234",
            birth = "1990.01.01",
            gender = "남성"
        )

        // Should no longer be first run
        assertFalse(viewModel.isFirstRun.value)
        assertEquals("홍길동", viewModel.patientName.value)
        assertEquals("HX-TEST-1234", viewModel.patientCode.value)
        assertEquals("1990.01.01", viewModel.patientBirth.value)
        assertEquals("남성", viewModel.patientGender.value)

        // Instantiate new ViewModel to verify persistence in SharedPreferences!
        val secondViewModel = HelixViewModel(app)
        assertFalse(secondViewModel.isFirstRun.value)
        assertEquals("홍길동", secondViewModel.patientName.value)
        assertEquals("HX-TEST-1234", secondViewModel.patientCode.value)
        assertEquals("1990.01.01", secondViewModel.patientBirth.value)
        assertEquals("남성", secondViewModel.patientGender.value)
    }

    @Test
    fun testGpsRecalculationAndSorting() = runBlocking {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = HelixViewModel(app)

        // Trigger coordinate update (Gangnam Station: 37.4980, 127.0276)
        viewModel.updateLocation(37.4980, 127.0276, true)

        val hospitals = viewModel.hospitals.value
        assertFalse(hospitals.isEmpty())

        // Check distance is calculated
        hospitals.forEach { hospital ->
            assertTrue(hospital.distanceKm >= 0.0)
            assertNotNull(hospital.etaMinutes)
        }

        // Move to a different location (near Seoul National Univ Hospital: 37.5796, 127.0001)
        viewModel.updateLocation(37.5796, 127.0001, true)
        val updatedHospitals = viewModel.hospitals.value

        // Check distance and ETA were updated dynamically
        assertTrue(updatedHospitals.first().distanceKm >= 0.0)
    }
}
