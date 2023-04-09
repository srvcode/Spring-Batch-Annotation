package com.srvcode.springbatch.config;

import java.net.MalformedURLException;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import com.srvcode.springbatch.model.FlightTicket;
import com.srvcode.springbatch.service.CustomItemProcessor;

public class SpringBatchConfig {

	@Autowired
	private JobBuilderFactory jobs;

	@Autowired
	private StepBuilderFactory steps;

	@Value("read/entries.csv")
	private Resource inputCsv;

	@Value("file:write/xml/result.xml")
	private Resource resultXml;

	@Bean
	public ItemReader<FlightTicket> itemReader() throws UnexpectedInputException, ParseException {

		FlatFileItemReader<FlightTicket> reader = new FlatFileItemReader<>();
		DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();

		String[] tokens = { "name", "ticketnumber", "route", "ticketprice" };

		tokenizer.setNames(tokens);
		reader.setResource(inputCsv);
		reader.setLinesToSkip(1);

		BeanWrapperFieldSetMapper<FlightTicket> beanWrapperFieldSetMapper = new BeanWrapperFieldSetMapper<>();
		beanWrapperFieldSetMapper.setTargetType(FlightTicket.class);

		DefaultLineMapper<FlightTicket> lineMapper = new DefaultLineMapper<>();
		lineMapper.setLineTokenizer(tokenizer);
		lineMapper.setFieldSetMapper(beanWrapperFieldSetMapper);

		reader.setLineMapper(lineMapper);
		return reader;
	}

	@Bean
	public ItemProcessor<FlightTicket, FlightTicket> itemProcessor() {
		return new CustomItemProcessor();
	}

	@Bean
	public ItemWriter<FlightTicket> itemWriter(Marshaller marshaller) throws MalformedURLException {

		StaxEventItemWriter<FlightTicket> itemWriter = new StaxEventItemWriter<>();
		itemWriter.setMarshaller(marshaller);
		itemWriter.setRootTagName("ticketRecords");
		itemWriter.setResource(resultXml);

		return itemWriter;
	}

	@Bean
	public Marshaller marshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setClassesToBeBound(FlightTicket.class);
		return marshaller;
	}

	@Bean
	protected Step step1(ItemReader<FlightTicket> reader, ItemProcessor<FlightTicket, FlightTicket> processor,
			ItemWriter<FlightTicket> writer) {
		return steps.get("step1").<FlightTicket, FlightTicket>chunk(10)
				.reader(reader).processor(processor)
				.writer(writer).build();
	}
	
	@Bean(name="CSVtoXML")
	public Job job(@Qualifier("step1") Step step1) {
		return jobs.get("CSVtoXML").start(step1).build();
	}
}
