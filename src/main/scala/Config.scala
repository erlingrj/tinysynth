package tinysynth
import chisel3._

trait TinySynthConfig {
  def numSynths = 1
  def clockPeriodNs = 20
  // PWM options
  // FIXME: What is the relationship between the next two.
  //  we should only have one and the other should be derived.
  def pwmOutputBits = 8
  def pwmCyclesPerPeriod = 50

  // Oscillator options
  def hasSineWaveOscillator = false
  def hasSquareWaveOscillator = true
  def hasSawtoothOscillator = false
  def numNotes = 128
  def oscPeriodBits = 32

  // Input ADC
  def adcNumPipelineRegs = 2

  // UI options
  def volumeBits = 8
  def uiKnobInputBits = pwmOutputBits

  // Convenience functions
  def numOscillatorTypes= {
    var cnt = 0
    if (hasSawtoothOscillator) cnt +=1
    if (hasSineWaveOscillator) cnt +=1
    if (hasSquareWaveOscillator) cnt += 1
    cnt
  }
}

object DefaultConfig extends TinySynthConfig {}

