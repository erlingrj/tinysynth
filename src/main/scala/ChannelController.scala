package tinysynth

import chisel3._
import chisel3.util._
import scala.math.pow


class ChannelController(cfg: TinySynthConfig) extends Module {
  val io = IO(new Bundle {
    val midiIn = Flipped(Valid(new MidiNoteOnOff()))
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

  val regPeriod = RegInit(0.U(cfg.oscPeriodBits.W))
  val regVolume = RegInit(0.U(cfg.volumeBits.W))
  val periodTable = VecInit(Seq.tabulate(cfg.numNotes)(i => freqToPeriod(midiNoteNumberToFreq(i)).U))

  io.oscillatorSelector := OscillatorType.SquareWave
  io.period := regPeriod
  io.volume := regVolume

  when(io.midiIn.valid) {
    io.period := periodTable(io.midiIn.bits.note)
    when (io.midiIn.bits.hdr.msgType === MIDI.NOTE_ON) {
      io.volume := io.midiIn.bits.velocity // FIXME: Can we just translate this?
    }.otherwise {
      io.volume := 0.U
    }
  }
}