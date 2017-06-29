package com.petrolink.mbe.rulesflow.variables;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jdom2.Element;

import com.petrolink.mbe.cache.CacheFactory;
import com.petrolink.mbe.directories.WellDirectory;
import com.petrolink.mbe.model.channel.DataPoint;
import com.petrolink.mbe.rulesflow.RuleFlow;
import com.petrolink.mbe.services.ServiceAccessor;
import com.smartnow.engine.Engine;
import com.smartnow.engine.exceptions.EngineException;

/**
 * Channel for Preloaded Channel Alias. These c hannel alias will load the channel contents from a file in the predefined folder
 * and keep them all in memory.
 * @author paul
 *
 */
public class PreloadedChannelAlias extends GlobalBufferedChannelAliasBase {
	Object lastUsedIndex;
	
	/* (non-Javadoc)
	 * @see com.petrolink.mbe.rulesflow.variables.BufferedChannelAlias#load(com.petrolink.mbe.rulesflow.RuleFlow, org.jdom2.Element)
	 */
	@Override
	public void load(RuleFlow rule, Element e) throws EngineException {
		super.load(rule, e);

		if (this.cache == null) {
			this.cache = CacheFactory.getInstance().getBufferedCache().getWell(rule.getWellId()).getOrCreateChannel(uuid);

			// Registrating Cache to Well
			WellDirectory wellDirectory = ServiceAccessor.getWellDirectory();
			if (wellDirectory != null) {
				wellDirectory.getWell(rule.getWellId()).registerChannel(uuid, this.getAlias());
			}
		}
		
		String folder = Engine.getInstance().getUserDefinedProperties().getProperty("PreloadedChannels");
		
		Path xlsfile = Paths.get(System.getProperty("user.dir") + folder + this.getUuid().toString() + ".xlsx");

		try {
			if (Files.exists(xlsfile)) {
				XSSFWorkbook workbook = new XSSFWorkbook(xlsfile.toFile());
				// Get first sheet from the workbook
				XSSFSheet sheet = workbook.getSheetAt(0);

				// Reading headers
				for (int i = 0; i < 50000; i++) {
					Row set = sheet.getRow(i);

					if (set == null)
						break;

					Cell idxCell = set.getCell(0);
					if ((idxCell == null) || (idxCell.getCellType() == Cell.CELL_TYPE_BLANK)) {
						break;
					} else if ((idxCell.getCellType() == Cell.CELL_TYPE_STRING)
							&& ((idxCell.getStringCellValue() == null) || (idxCell.getStringCellValue().equals("")))) {
						break;
					}

					Object index;
					if (idxCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
						index = idxCell.getNumericCellValue();
					} else {
						String idxStringValue = idxCell.getStringCellValue();
						OffsetDateTime idxParsed = OffsetDateTime.parse(idxStringValue);

						index = idxParsed;
					}

					Cell channelValue = set.getCell(1);

					if ((channelValue != null) && (channelValue.getCellType() != Cell.CELL_TYPE_BLANK)
							&& (channelValue.getCellType() == Cell.CELL_TYPE_NUMERIC)) {
						Double value = channelValue.getNumericCellValue();
						
						DataPoint dp = new DataPoint(index, value);
						this.cache.setMaxSize(i+1);
						this.cache.addDataPoint(dp);
					} 

				}

				workbook.close();
			} else {
				logger.error("Preloaded Channel data file not found {}", xlsfile.toString());
			}
		} catch (IOException ex) {
			logger.error("IO Exception while opening the file", ex);
		} catch (InvalidFormatException ex) {
			logger.error("Format Exception while opening the file", ex);
		}
	}
}
