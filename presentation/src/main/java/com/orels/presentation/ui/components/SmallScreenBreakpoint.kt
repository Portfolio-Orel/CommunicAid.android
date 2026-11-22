package com.orels.presentation.ui.components

/**
 * Screens whose HEIGHT is below this dp value are treated as "compact" and get the
 * hamburger drawer instead of the bottom nav bar.
 *
 * Examples (Pixel 9 Pro Fold):
 *  - CLOSED  cover screen : ~400 dp tall  →  hamburger ✅
 *  - OPEN  inner screen   : ~900 dp tall  →  bottom bar ✅
 */
const val COMPACT_HEIGHT_DP = 700
