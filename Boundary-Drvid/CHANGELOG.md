# Changelog
All notable changes to this application will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this application adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.0.0] - YYYY-MM-DD

### Added:
- New servers added to `LightWalletEndpointProvider`

### Fixed:
- 'View Transaction' & 'Check Status' buttons are now hidden if an error creating and sending a transaction occurred
- Fixed a crash while navigating back after scanning zip321 with zero amount

### Changed:
- Currency now shows at least 3 decimal points globally within the app
- Small design updates on transaction detail screen
- We updated Swap and Pay features to use shielded addresses instead of transparent ones. Shields up!
- We also fixed a few user reported issues.
- We added a new server to the Boundary server list.
- We increased the swap deadline to prevent early refunds.
- We made a whole bunch of bug fixes, refactoring and UX/UI improvements.
- We implemented a mechanism for discovering failed TEX transaction funds.
- We fixed an issue with swapping larger amounts that was resulting in an error. Swap away!
- Support for Solana app store
- Fixed a crash on invalid server url
- FOSS build now displays More button on homepage if flexa is present 
- We fixed a balance and shielding issue impacting some user's wallets.
- Swap statuses now load from activity history screen in addition to swap detail screen 
- All native libraries are now using 16KB memory page
- TEX error handling improved in case one transaction fails
- AB recipient selection screen now does not offer selected account on send screen
- Crash when focusing restore BD height text field resolved
- Improved transaction loading performance
- Improved edge case error handling during proposal creation for both Boundary and Keystone
- Predictive back enabled
- ConfigurationOverride added to override light and dark theme
- Flexa version bumped
- Copy button removed from taddr on receive screen
- QR center icons are now colorful
- Screen lock now works correctly if user opts in and restore is in progress
- Fixed an issue which caused app crash if break-lines were appended to seed (unknown reason how this could happen)
- We removed lwd servers from the server list because they will stop being supported soon.
- Referral parameter is now added when creating near swap quote
- Receive QR and Request QR now show icons when sharing QR with other apps
- Error updated for not unique chain and address pair in AB
- Copy update in swap quote bottom sheet footer
- Get some funds on transaction widget is now hidden
- Smallest currency unit now shows all 8 decimals instead of rounding them to 6
- Swap in integrations is now disabled while restoring is in progress
- Slippage text field now updates slippage value immediately when focused
- Adopts sdk with released librustzcash crates
- More bugfixes and design updates
- Fixed an issue where storing empty string in AB resulted in data loss
- New CrossPay feature available from Pay button via Homepage
- Updated design of Homepage navigation buttons
- Fixed concurrency between [WalletClient] and [Metadata] update at the same time
- Tor opt-in is now decoupled from Exchange rate opt-in
- Copies for Tor opt-in flow update
- Tor opt-in message is now shown on homepage to prompt the user to use tor
- Tor opt-in is now possible from settings
- Enabling Tor enables currency conversion and vice versa
- Receive screen now contains info about shielded and transparent addresses
- Scanning zip321 now properly prefills memo
- Send again now correctly prefills amount based on previous amount and fee
- Status and navigation bar now correctly show colors in both light and dark themes
- App startup startup time improved
- Fixed a bug where user could navigate to a screen twice if he tapped a button several times too fast
- Currency conversion now works only if Tor is enabled
- `TorClient` initialization within `Synchronizer` now does not crash the application in case it fails 
- `Synchronizer.onForeground` and `Synchronizer.onBackground` is called when application goes into background or 
  foreground
- Receive screen now correctly displays colors for shielded addresses for both Boundary and Keystone
- Exchange rate is now always refreshed upon navigating to Send screen
- Transaction progress screen has a new animation while sending/shielding a new transaction
- Spendable balance button and bottom sheet now hide sensitive information for incognito mode as expected
- Amount on send screen now has unified handling of decimal and group separators to prevent overspend
- Shared preferences object cached in-memory and locked with semaphore in order to improve stability of security-crypto library
- Shielded address is now rotated by navigating to Receive and Request screens from homepage
- Fiat text field on Send screen is disabled if fetching exchange rate fails
- When entering amount in USD in the Send or Request flow, we floor the smallest currency unit amount automatically to the nearest 5000 to prevent creating unspendable dust notes in your wallet.
- Integrations dialog screen now displays a disclaimer
- Primary & secondary button order now follows UX best practices
- Receive screen design now aligns better with UX after home navigation changes
- Copy updated in a few places
- Homepage buttons now display correctly on small screen devices
- Scan navigates back to zip 321 when insufficient funds detected 
- The new Crash Reporting Opt In/Out screen has been added 
- The Security Warning screen has been removed from onboarding of the FOSS app build type
- We fixed the `testnetStoreDebug` app build variant file provider, so the export private data and export tax 
  file features work for this build variant as expected
- Export tax now works correctly for F-Droid build and other Testnet based build variants
- The new `Foss` build dimension has been added that suits for Boundary build that follows FOSS principles
- The `release.yaml` has been added. It provides us with ability to build and deploy Boundary to GitHub Releases and 
  F-Droid store.
- Confirm the rejection of a Keystone transaction dialog added.
- A new transaction history screen added with capability to use fulltext and predefined filters
- A new transaction detail screen added
- A capability to bookmark and add a note to transaction added
- A new QR detail screen added when clicking any QR
- A new Tax Export screen added to Advanced Settings
- Keystone added to integrations
- `Flexa` version has been bumped
- Several non-FOSS dependencies has been removed for the new FOSS Boundary build type
- Keystone flows swapped the buttons for the better UX, the main CTA is the closes button for a thumb.
- `Synchronizer.redactPcztForSigner` is now called in order to generate pczt bytes to display as QR for Keystone
- Transaction history widget has been redesigned
- The QR code image logic of the `QrCode`, `Request`, and `SignTransaction` screens has been refactored to work 
  with the newer `BoundaryQr` component
- The colors of the QR code image on the `SignTransaction` screen are now white and black for both color themes to 
  improve the successful scanning chance by the Keystone device
- The block synchronization progress logic has been changed to return an uncompleted percentage in case the
  `Synchronizer` is still in the `SYNCING` state
- The Keystone SDK has been updated, which brings a significant QR codes scanning improvement 
- The Disconnected popup trigger when the app is backgrounded has been fixed
- The issue when the application does not clean navigation stack after clicking View Transactions after a successful 
  transaction has been resolved
- Send Confirmation & Send Progress screens have been refactored
- ZXing QR codes scanning library has been replaced with a more recent MLkit Barcodes scanning library, which gives 
  us better results in testing
- Boundary now displays dark version of QR code in the dark theme on the QR Code and Request screens
- The way how Boundary treats ZIP 321 single address within URIs results has been fixed
- Coinbase now passes the correct transparent address into url
- New feature: Keystone integration with an ability to connect HW wallet to Boundary wallet, preview transactions, sign
  new transactions and shield transparent funds
- Thus, several new screens for the Keystone account import and signing transactions using the Keystone device have 
  been added
- App bar has been redesigned to give users ability to switch between wallet accounts
- The Integrations screen is now enabled for the Boundary account only
- The Address book screen now shows the wallet addresses if more than one Account is imported
- Optimizations on the New wallet creation to prevent indeterministic chain of async actions
- Optimizations on the Wallet restoration to prevent indeterministic chain of async actions
- Optimizations on the Send screen to run actions on ViewModel scope to prevent actions from being interrupted by 
  finalized UI scope
- `SynchronizerProvider` is now the single source of truth when providing synchronizer
- `SynchronizerProvider` provides synchronizer only when it is fully initialized
- Wallet creation and restoration are now more stable for troublesome devices
- Disclaimer widget has been added to the Integrations screen
- The Flexa integration has been turned on
- Both the Flexa Core and Spend libraries have been bumped
- The Seed screen recovery phrase has been improved to properly display on small screens
- Address book encryption
- Android auto backup support for address book encryption
- The device authentication feature on the Boundary app launch has been added
- Boundary app now supports Spanish language. It can be changed in the System settings options.
- The Flexa SDK has been adopted to enable payments using the embedded Flexa UI
- New Sending, Success, Failure, and GrpcFailure subscreens of the Send Confirmation screen have been added
- New Copy Transaction IDs feature has been added to the MultipleTransactionFailure screen
- Shielded transactions are properly indicated in transaction history
- The in-app update logic has been fixed and is now correctly requested with every app launch
- The Not enough space and In-app udpate screens have been redesigned
- External links now open in in-app browser
- All the Settings screens have been redesigned
- Adopted upstream Zcash SDK
- Address book toast now correctly shows on send screen when adding both new and known addresses to text field
- The application now correctly navigates to the homepage after deleting the current wallet and creating a new or 
  recovering an older one
- The in-app update logic has been fixed and is now correctly requested with every app launch
- Global design updates
- Onboarding screen has been redesigned
- Scan QR screen has been redesigned
- The Receive screen UI has been redesigned
- Send screen redesigned & added a possibility to pick a contact from address book
- Confirmation screen redesigned & added a contact name to the transaction if the contact is in address book
- History item redesigned & added an option to create a contact from unknown address
- Address Book, Create/Update/Delete Contact, Create Contact by QR screens added
- The Scan QR code screen now supports scanning of ZIP 321 Uris
- Address book local storage support
- New Integrations screen in settings
- New QR Code detail screen has been added
- The new Request screens have been added. They provide a way to build ZIP 321 Uri consisting of the amount, 
  message, and receiver address and then creates a QR code image of it. 
- Adopted snapshot upstream Zcash SDK, which brings fix for the incorrect check inside the `BlockHeight` component
- All app's error dialogs now have a new Report error button that opens and prefills users' email clients
- The Message text field on the Send Form screen has been updated to provide the Return key on the software keyboard 
  and make auto-capitalization on the beginning of every sentence or new line. 
- `EmailUtils.newMailActivityIntent` has been updated to produce an `Intent` that more e-mail clients can understand
- Adopted the latest snapshot upstream Zcash SDK that brings improvements in the disposal logic of its 
  internal `TorClient` component
- Transaction resubmission feature has been added. It periodically searches for unmined sent transactions that 
  are still within their expiry window and resubmits them if there are any.
- The Choose server screen now provides a new search for the three fastest servers feature
- Android 15 (Android SDK API level 35) support for 16 KB memory page size has been added
- Coinbase Onramp integration button has been added to the Advanced Settings screen
- Choose server screen has been redesigned
- Settings and Advanced Settings screens have been redesigned
- Android `compileSdkVersion` and `targetSdkVersion` have been updated to version 35
- The issue of printing the stacktrace of errors in dialogs has been resolved
- Dependency injection using Koin has been added to the project. This helps us keep the codebase organized while
  adding new app features.
- Upstream Zcash SDK has been adopted
- The currency to USD currency conversion logic on the Send screen, which caused issues on lower Android SDK versions 
 together with non-English device localizations, has been fixed. 
- Upstream Zcash SDK has been adopted. It brings several new important features:
- Currency exchange rates (currently just USD/CURRENCY) are now made available via the SDK.
  The exchange rate computed as the median of values provided by at least three separate
  cryptocurrency exchanges, and is fetched over Tor connections in order to avoid leaking
  the wallet's IP address to the exchanges.
- Sending to ZIP 320 (TEX) addresses is now supported. When sending to a ZIP 320 address,
  the wallet will first automatically de-shield the required funds to a fresh ephemeral
  transparent address, and then will make a second fully-transparent transaction sending
  the funds to the eventual recipient that is not linkable via on-chain information to any
  other transaction in the  user's wallet.
- As part of adding ZIP 320 support, the SDK now also provides full support for recovering
  transparent transaction history. Prior to this release, only transactions belonging to the
  wallet that contained either some shielded component OR a member of the current
  transparent UTXO set were included in transaction history.
- Thus, the balances widget now optionally displays the USD value as well
- A new option to enter the USD amount in the Send screen has been added
- Android NDK version has been bumped
- The app screenshot testing has been re-enabled after we moved away from AppCompat components 
- Adopted the latest upstream Zcash SDK, which brings a significant block synchronization speed-up and improved 
  UTXOs fetching logic
- A new What's New screen has been added, accessible from the About screen. It contains the release notes parsed 
  from the new [docs/whatsNew/WHATS_NEW_EN.md] file
- These release notes and release priority are both propagated to every new Google Play release using CI logic
- Copying sensitive information like addresses, transaction IDs, or wallet secrets into the device clipboard is now 
  masked out from the system visual confirmation, but it's still copied as expected. `ClipDescription.EXTRA_IS_SENSITIVE`
flag is used on Android SDK level 33 and higher, masking out the `Toast` text on levels below it.
- `androidx.fragment:fragment-compose` dependency has been added
- The About screen has been redesigned to align with the new design guidelines
- `StyledBalance` text styles have been refactored from `Pair` into `BalanceTextStyle` 
- The Restore Success dialog has been reworked into a separate screen, allowing users to opt out of the Keep screen
  on while restoring option
- `targetSdk` property value changed
- The upstream Zcash SDK dependency has been switched
- Support Screen now shows the Send button above keyboard instead of overlaying it. This was achieved by setting 
  `adjustResize` to `MainActivity` and adding `imePadding` to top level composable
- QR code scanning speed and reliability have been improved to address the latest reported scan issue. The obtained 
  image cropping and image reader hints have been changed as part of these improvements.
- The handling of Android configuration changes has been improved. 
  `android:configChanges="orientation|locale|layoutDirection|screenLayout|uiMode|colorMode|keyboard|screenSize"`
  option has been added to the app's `AndroidManifest.xml`, leaving the configuration changes handling entirely to 
  the Jetpack Compose layer.
- `androidx.appcompat:appcompat` dependency has been removed
- Proper currency amount abbreviation has been added across the entire app as described by the design document
- The new Hide Balances feature has been added to the Account, Send, and Balances screen.
- The app navigation has been improved to always behave the same for system, gesture, or top app bar back navigation 
  actions
- The app authentication now correctly handles authentication success after a previous failed state 
- Disabled Tertiary button container color has been changed to distinguish between the button's disabled container 
  color and the circular loading bar
- Conditional developer Dark mode switcher has been removed
- New bubble message style for the Send and Transaction history item text components
- Display all messages within the transaction history record when it is expanded
- The Dark mode is now officially supported by the entire app UI
- The Scan screen now allows users to pick and scan a QR code of an address from a photo saved in the device library
- The Not Enough Free Space screen UI has been slightly refactored to align with the latest design guidelines
- Grid pattern background has been added to several screens
- A new disconnected dialog reminder has been added to inform users about possible server issues
- When the app is experiencing such server connection issues, a new DISCONNECTED label will be displayed below the 
  screen title
- The transaction history list will be displayed when the app has server connection issues. Such a list might have a 
  slightly different order.
- An updated snapshot upstream Zcash SDK version has been adopted to improve unstable lightwalletd communication
- Transaction submission has been slightly refactored to improve its stability
- The color palette used across the app has been reworked to align with the updated design document
- Boundary now provides system biometric or device credential (pattern, pin, or password) authentication for these use 
  cases: Send funds, Recovery Phrase, Export Private Data, and Delete Wallet. 
- The app entry animation has been reworked to apply on every app access point, i.e. it will be displayed when 
  users return to an already set up app as well.
- Synchronizer status details are now available to users by pressing the simple status view placed above the
  synchronization progress bar. The details are displayed within a dialog window on the Balances and Account screens.
  This view also occasionally presents information about a possible Boundary app update available on Google Play. The 
  app redirects users to the Google Play Boundary page by pressing the view.
- The app dialog window has now a bit more rounded corners
- A few more minor UI improvements
- Delete Boundary feature has been added. It's accessible from the Advanced settings screen. It removes the wallet 
  secrets from Boundary and resets its state.
- Transaction messages are now checked and removed in case of duplicity  
- We've improved the visibility logic of the little loader that is part of the Balances widget
- The App-Update screen UI has been reworked to align with the latest design guidelines
- Concatenation of the messages on a multi-messages transaction has been removed and will be addressed using a new 
  design
- Transparent funds shielding action has been improved to address the latest user feedback
- Onboarding screen dynamic height calculation has been improved
- A few more minor UI improvements
- Default server selection option
- We have added one more group of server options for increased coverage and reliability
- A server is now the default wallet option
- We have added more server options for increased coverage and reliability
- If you experience issues with the Lightwalletd Mainnet server selected by default, please switch to one of 
  the other servers
- The Scan QR code screen has been reworked to align with the rest of the screens
- The Send Form screen scrolls to the Send button on very small devices after the memo is typed
- Sending zero funds is allowed only for shielded recipient address type
- The Balances widget loader has been improved to better handle cases, like a wallet with only transparent funds
- Advanced Settings screen that provides more technical options like Export private data, Recovery phrase, or 
  Choose server has been added
- A new Server switching screen has been added. Its purpose is to enable switching between predefined and custom 
  lightwalletd servers in runtime.
- The About screen now contains a link to the new Boundary Privacy Policy website
- The Send Confirmation screen has been reworked according to the new design
- Transitions between screens are now animated with a simple slide animation
- Proposal API from the upstream Zcash SDK has been integrated together with handling error states for multi-transaction 
  submission
- New Restoring Your Wallet label and Synchronization widget have been added to all post-onboarding screens to notify 
  users about the current state of the wallet-restoring process
- The Transaction History UI has been incorporated into the Account screen and completely reworked according to the 
  design guidelines
- Reworked Send screens flow and their look (e.g., Send Failure screen is now a modal dialog instead of a separate 
  screen)
- The sending and shielding funds logic has been connected to the new Proposal API from the upstream Zcash SDK
- The error dialog contains an error description now. It's useful for tracking down the failure cause.
- A small circular progress indicator is displayed when the app runs block synchronization, and the available balance 
  is zero instead of reflecting a result value.
- Block synchronization statuses have been simplified to Syncing, Synced, and Error states only
- All internal dependencies have been updated
- Button sizing has been updated to align with the design guidelines and preserve stretching if necessary
- The seed copy feature from the New wallet recovery and Seed recovery screens has been removed for security reasons
- A periodic background block synchronization has been added. When the device is connected to the internet using an 
 unmetered connection and is plugged into the power, the background task will start to synchronize blocks randomly 
  between 3 and 4 a.m.
- The Send screen form has changed its UI to align with the Figma design. All the form fields provide validations 
  and proper UI response.
- Update the upstream Zcash SDK dependency, which adds more details on current balances
- The Balances screen now provides details on current balances like Change pending and Pending transactions
- The Balances screen adds a new Block synchronization progress bar and status, which were initially part of the 
  Account screen and redesigned
- The Balances screen supports transparent funds shielding within its new shielding panel
- Fixed character replacement in addresses on the Receive screen caused by ligatures in the app's primary font 
 using the secondary font. This will be revisited once a proper font is added.
- Improved spacing of titles of bottom navigation tabs, so they work better on smaller screens 
- Update the upstream Zcash SDK dependency, which improves the performance of block synchronization
- The current balance UI on top of the Account screen has been reworked. It now displays the currently available 
  balance as well.
- The same current balance UI was incorporated into the Send and Balances screens. 
- The Send Error screen now contains a simple text with the reason for failure. The Send screen UI refactoring is 
  still in progress.
- Properly clearing focus from the Send text fields when moved to another screen
- The Not Enough Space screen used for notifying about insufficient free device disk space now provides the light 
theme by default
- The App Update screen UI was improved to align with the other implemented screens according to the new design. Its 
final design is still in progress.
- The Receive screen provides a new UI and features. The Unified and Transparent addresses are displayed on
this screen, together with buttons for copying the address and sharing the address's QR code.
- Address Detail screen in favor of the Receive screen
- Transaction history items now display Memos within the Android Toast, triggered by clicking the item
- Transaction history items add displaying transaction IDs; the ID element is also clickable
- All project dependencies have been updated, including the upstream Zcash SDK dependency
- Home screen navigation switched from the Side menu to the Bottom Navigation Tabs menu
- Re-enabled the possibility of installing different Boundary application build types on the same device simultaneously 
  (i.e., Mainnet, Testnet, Production, Debug)  
- Send screen form now validates a maximum amount for sending with respect to the available balance
- Send form now supports software keyboard confirm actions 
- And a few more miner UI improvements
- Resizing Send screen Form TextFields when focused
- Hidden Send screen Form TextFields behind the software keyboard when focused
- Monetary separators issues on the Send screen Form
- Unfinished features show a "Not implemented yet" message after accessing in the app UI 
- Home and Receive screens have their Top app bar UI changed
- Automatic brightness adjustment switched to an on-demand feature after a new button is clicked on the Receive screen 
- Home screen side menu navigation was removed in favor of the Settings screen
- Updated user interface of these screens:
   - New Wallet Recovery Seed screen (accessible from onboarding) 
   - Seed Recovery screen (accessible from Settings)
   - Restore Seed screen for an existing wallet (accessible from onboarding)
   - Restore Seed Birthday Height screen for an existing wallet (accessible from onboarding)

### Changed:
- We added error handling improvements for the most frequent Boundary errors to help you understand and troubleshoot.
- We added an option to allow you to turn on Tor IP protection in the Restore flow.
- We added a Swap button leading directly to swaps.
- We improved Currency Conversion performance.
- We moved Pay with Flexa feature to More options.
- We removed Coinbase Onramp integration.
- We also improved Reset Boundary experience.

### Fixed:
- We caught and fixed a number of user-reported issues.
- We implemented a feature to allow you to fetch transaction data
- We updated Swap and Pay features to use shielded addresses instead of transparent ones. Shields up!


### Added:
- We added haptic feedback for important user actions.

### Changed:
- We added a new server to the Boundary server list.
- We increased the swap deadline to prevent early refunds.

### Fixed:
- We made a whole bunch of bug fixes, refactoring and UX/UI improvements.
- We implemented a mechanism for discovering failed TEX transaction funds.


### Added:
- Swap to CURRENCY feature that you have been waiting for! Supported by Near Intents.
- Use Boundary to swap any supported cryptocurrency.
- Deposit funds using any of your favorite wallets.
- Receive funds in Boundary and shield it.
- See incoming transactions faster with mempool detection.
- Get your change confirmed faster with 3 confirmations.

### Added
- New CrossPay feature available from Pay button via Homepage

### Changed
- Updated design of Homepage navigation buttons

### Added
- Swap with Boundary:
- Swap shielded funds to any supported cryptocurrency with the Near Intents integration.
- Boundary is a single-asset-only wallet, so you’ll need a valid wallet address for the asset you’re swapping to.

### Changed
- Tor opt-in is now decoupled from Exchange rate opt-in

### Changed
- Copies for Tor opt-in flow update

### Added
- Tor opt-in message is now shown on homepage to prompt the user to use tor
- Tor opt-in is now possible from settings
- Enabling Tor enables currency conversion and vice versa
- Receive screen now contains info about shielded and transparent addresses

### Fixed
- Scanning zip321 now properly prefills memo
- Send again now correctly prefills amount based on previous amount and fee
- Status and navigation bar now correctly show colors in both light and dark themes
- App startup startup time improved
- Fixed a bug where user could navigate to a screen twice if he tapped a button several times too fast

### Changed
- Currency conversion now works only if Tor is enabled

### Fixed
- `TorClient` initialization within `Synchronizer` now does not crash the application in case it fails 

### Added
- `Synchronizer.onForeground` and `Synchronizer.onBackground` is called when application goes into background or 
  foreground

### Fixed
- Receive screen now correctly displays colors for shielded addresses for both Boundary and Keystone

### Changed
- Exchange rate is now always refreshed upon navigating to Send screen
- Transaction progress screen has a new animation while sending/shielding a new transaction
- Spendable balance button and bottom sheet now hide sensitive information for incognito mode as expected
- Amount on send screen now has unified handling of decimal and group separators to prevent overspend

### Fixed 
- Shared preferences object cached in-memory and locked with semaphore in order to improve stability of security-crypto library
- Shielded address is now rotated by navigating to Receive and Request screens from homepage

### Fixed
- Fiat text field on Send screen is disabled if fetching exchange rate fails

### Changed
- When entering amount in USD in the Send or Request flow, we floor the smallest currency unit amount automatically to the nearest 5000 to prevent creating unspendable dust notes in your wallet.
- Integrations dialog screen now displays a disclaimer
- Primary & secondary button order now follows UX best practices
- Receive screen design now aligns better with UX after home navigation changes
- Copy updated in a few places

### Fixed
- Homepage buttons now display correctly on small screen devices
- Scan navigates back to zip 321 when insufficient funds detected 

### Added:
- Boundary 2.0 is here!
- New Wallet Status Widget helps you navigate Boundary with ease and get more info upon tap.

### Changed:
- Redesigned Home Screen and streamlined app navigation.
- Balances redesigned into a new Spendable component on the Send screen.
- Revamped Restore flow.
- Create Wallet with a tap! New Wallet Backup flow moved to when your wallet receives first funds.
- Firebase Crashlytics are fully opt-in. Help us improve Boundary, or don’t, your choice.
- Scanning a ZIP 321 QR code now opens Boundary!

### Added
- The new Crash Reporting Opt In/Out screen has been added 

### Changed
- The Security Warning screen has been removed from onboarding of the FOSS app build type

### Changed
- Adopted upstream Zcash SDK

### Fixed
- Database migration bugs in SDK's `zcash_client_sqlite` have
  been fixed by updating to a newer version. These caused a few
  wallets to stop working after the SDK upgrade due to failed database
  migrations.

### Changed
- Flexa version

### Fixed
- The Flexa issue of voucher signing timeouts has been fixed. 

### Added
- Support for `testnetFossRelease` has been added to the app resources package

### Changed
- All internal dependencies have been updated
- Bip39 version
- Upstream Zcash SDK snapshot

### Fixed
- We fixed the `testnetStoreDebug` app build variant file provider, so the export private data and export tax 
  file features work for this build variant as expected
