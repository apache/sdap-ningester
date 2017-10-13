package gov.nasa.jpl.nexus.ningester.datatiler;

import org.nasa.jpl.nexus.ingest.wiretypes.NexusContent;
import org.springframework.batch.item.*;

public class NetCDFItemReader implements ItemReader<NexusContent.NexusTile>, ItemStream {
  @Override
  public NexusContent.NexusTile read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
    return null;
  }

  @Override
  public void open(ExecutionContext executionContext) throws ItemStreamException {

  }

  @Override
  public void update(ExecutionContext executionContext) throws ItemStreamException {

  }

  @Override
  public void close() throws ItemStreamException {

  }
}
