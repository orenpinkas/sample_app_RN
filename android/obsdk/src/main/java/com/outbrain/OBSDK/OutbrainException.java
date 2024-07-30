package com.outbrain.OBSDK;

/**
 * @brief A wrapper class for RuntimeException, thrown if Outbrain encounters an error while performing an operation.
 */
public class OutbrainException extends RuntimeException {
  public OutbrainException(String detailMessage) {
    super(detailMessage);
  }
  public OutbrainException(Exception ex) {
    super(ex);
  }
}
