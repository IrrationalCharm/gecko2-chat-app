package eu.irrationalcharm.messaging_service.enums;

public enum MessageType {
    //Incoming messages from client
    CHAT_MESSAGE_CLIENT, //Incoming message to server
    READ_RECEIPT_CLIENT, //Client read a message
    DELIVERY_RECEIPT_CLIENT, //Confirmation that client received a message
    TYPING_STATUS_CLIENT, //Client is typing


    //Outgoing messages
    CHAT_MESSAGE_SERVER, //Outgoing message from server
    FRIEND_REQUEST_SERVER, //Outgoing friend request from server
    FRIEND_REQUEST_ACCEPTED_SERVER,
    MESSAGE_DELIVERED_SERVER, //Acknowledgment that recipient received message
    MESSAGE_READ_SERVER, //Acknowledgment that recipient read message
    MESSAGE_SENT_SERVER //Acknowledgment that server received message
}
