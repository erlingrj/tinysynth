package tinysynth

import chisel3._
import chisel3.util._
import scala.math.pow


class ChannelController(cfg: TinySynthConfig) extends Module {
  val io = IO(new Bundle {
    val freqKnob = Input(UInt(cfg.uiKnobInputBits.W))
    val oscillatorSelector = Output(OscillatorType())
    val period = Output(UInt(cfg.oscPeriodBits.W))
    val volume = Output(UInt(cfg.volumeBits.W))
  })

  def midiNoteNumberToFreq(midi: Int) = {
    440.0 * pow(2, (midi-69)/12)
  }
  def freqToPeriod(freq: Double) = {
    val periodNs = 1000000000/freq
    val periodCC = periodNs/cfg.clockPeriodNs
    periodCC.toInt
  }

  // Shift the freq select knob to range the 7 bits needed for a MIDI note
  require(cfg.uiKnobInputBits >= 7)
  val freqSelect = (io.freqKnob >> (cfg.uiKnobInputBits - 7)).asUInt
  val periodTable = VecInit(Seq.tabulate(128)(i => freqToPeriod(midiNoteNumberToFreq(i)).U))

  io.oscillatorSelector := OscillatorType.SquareWave
  io.period := periodTable(freqSelect)// Just output an A for now
  io.volume := 128.U // Set to half volume
}