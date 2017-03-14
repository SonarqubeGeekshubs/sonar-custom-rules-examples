/**
 * This file is the sample code against we run our unit test. It is placed
 * src/test/files in order to not be part of the maven compilation.
 **/
class AvoidMaskedSwitchCheck {

  double getSpeed(int value) {
    if (value == EUROPEAN) {
      return getBaseSpeed();
    } else if (value == AFRICAN) { // Noncompliant
      return getBaseSpeed() - getLoadFactor() * _numberOfCoconuts;
    } else if (value == NORWEGIAN_BLUE) { // Noncompliant
      return (_isNailed) ? 0 : getBaseSpeed(_voltage);
    } else {
      throw new RuntimeException ("Should be unreachable");
    }
  }
}