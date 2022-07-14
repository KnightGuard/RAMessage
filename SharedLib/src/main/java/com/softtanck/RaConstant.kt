package com.softtanck

/**
 * @author Softtanck
 * @date 2022/3/12
 * Description: TODO
 */
const val MESSAGE_BASE_KEY = 1

const val MESSAGE_REGISTER_CLIENT_REQ = MESSAGE_BASE_KEY + 1
const val MESSAGE_REGISTER_CLIENT_RSP = MESSAGE_BASE_KEY + 2
const val MESSAGE_CLIENT_DISCONNECT_REQ = MESSAGE_BASE_KEY + 3

const val MESSAGE_CLIENT_SINGLE_REQ = MESSAGE_BASE_KEY + 4
const val MESSAGE_CLIENT_SINGLE_RSP = MESSAGE_BASE_KEY + 5

/**
 * Sent from the server to the client to indicate the something changed.
 */
const val MESSAGE_CLIENT_BROADCAST_RSP = MESSAGE_BASE_KEY + 6

const val MESSAGE_BUNDLE_REPLY_TO_KEY = "message_bundle_reply_to_key"
const val MESSAGE_BUNDLE_METHOD_NAME_KEY = "message_bundle_method_name_key"
const val MESSAGE_BUNDLE_NORMAL_RSP_KEY = "message_bundle_rsp_key"
const val MESSAGE_BUNDLE_RSP_TYPE_KEY = "message_bundle_rsp_type_key"
const val MESSAGE_BUNDLE_TYPE_ARG_KEY = "message_bundle_type_arg_key"
const val MESSAGE_BUNDLE_TYPE_PARAMETER_KEY = "message_bundle_type_parameter_key"


const val DEFAULT_BUNDLE_TYPE = 0
const val MESSAGE_BUNDLE_PARCELABLE_TYPE = DEFAULT_BUNDLE_TYPE
const val MESSAGE_BUNDLE_ARRAYLIST_TYPE = DEFAULT_BUNDLE_TYPE + 1
const val MESSAGE_BUNDLE_BOOLEAN_TYPE = DEFAULT_BUNDLE_TYPE + 2
const val MESSAGE_BUNDLE_CHAR_TYPE = DEFAULT_BUNDLE_TYPE + 3
const val MESSAGE_BUNDLE_STRING_TYPE = DEFAULT_BUNDLE_TYPE + 4
const val MESSAGE_BUNDLE_BYTE_TYPE = DEFAULT_BUNDLE_TYPE + 5


