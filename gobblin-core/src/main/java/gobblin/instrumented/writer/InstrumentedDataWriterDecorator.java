/*
 * (c) 2014 LinkedIn Corp. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the
 * License at  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.
 */

package gobblin.instrumented.writer;

import java.io.IOException;

import gobblin.configuration.State;
import gobblin.metrics.MetricContext;
import gobblin.writer.DataWriter;


/**
 * Decorator that automatically instruments {@link gobblin.writer.DataWriter}.
 * Handles already instrumented {@link gobblin.instrumented.writer.InstrumentedDataWriter} appropriately to
 * avoid double metric reporting.
 */
public class InstrumentedDataWriterDecorator<D> extends InstrumentedDataWriterBase<D> {

  private DataWriter<D> embeddedWriter;
  private boolean isEmbeddedInstrumented;

  public InstrumentedDataWriterDecorator(DataWriter<D> writer, State state) {
    super(state);
    this.embeddedWriter = writer;
    this.isEmbeddedInstrumented = InstrumentedDataWriterBase.class.isInstance(writer);
  }

  @Override
  public MetricContext getMetricContext() {
    return this.isEmbeddedInstrumented ?
        ((InstrumentedDataWriterBase)embeddedWriter).getMetricContext() :
        super.getMetricContext();
  }

  @Override
  public void write(D record)
      throws IOException {
    if(this.isEmbeddedInstrumented) {
      embeddedWriter.write(record);
    } else {
      super.write(record);
    }
  }

  @Override
  public void writeImpl(D record)
      throws IOException {
    embeddedWriter.write(record);
  }

  @Override
  public void commit()
      throws IOException {
    embeddedWriter.commit();
  }

  @Override
  public void cleanup()
      throws IOException {
    embeddedWriter.cleanup();
  }

  @Override
  public long recordsWritten() {
    return embeddedWriter.recordsWritten();
  }

  @Override
  public long bytesWritten()
      throws IOException {
    return embeddedWriter.bytesWritten();
  }
}
