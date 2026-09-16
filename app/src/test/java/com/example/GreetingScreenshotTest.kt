package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.mesh.NetworkConnectionState
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.ZeroGridTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [34])
class GreetingScreenshotTest {

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun greeting_screenshot() {
        composeTestRule.setContent {
            ZeroGridTheme(darkTheme = true) {
                HomeScreen(
                    networkState = NetworkConnectionState.OFFLINE_MESH,
                    connectedCount = 8,
                    nearestDistanceMeters = 320,
                    connectionTransport = "Wi-Fi Direct",
                    myNodeId = "NODE-DEMO",
                    countryCode = "IN",
                    language = "bn",
                    isMetric = true,
                    isRelayMode = true,
                    onNavigateToChat = {},
                    onNavigateToNearby = {},
                    onNavigateToMap = {},
                    onNavigateToLocation = {},
                    onNavigateToSos = {},
                    onNavigateToFiles = {},
                    onNavigateToCommunity = {},
                    onNavigateToDiagnostics = {},
                    onNavigateToTwoPhoneTest = {},
                    onNavigateToPermissionCenter = {}
                )
            }
        }

        composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
    }
}
