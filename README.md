android-dynamic-ui
==================

Dynamic UI components in Android.

This project tries to show how it is possible to manage and adapt dynamic user interface
components (e.g., Buttons, TextViews, EditText...) to user/context/device current needs
(in Android, of course).

To do this I have create a ProxyView component. This component extends from android.View,
implements its methods, and behaves corresponding with the View component we want it to
be. This way, we can tell the ProxyView in the layout.xml file to be a "button", or a
"edittext", and it will take the corrsponding appareance and beaviour.
