import * as fs from 'fs';
import * as path from 'path';

const BASE_URL = 'https://kotlin.github.io/dataframe';

function extractTitle(filePath) {
    try {
        const content = fs.readFileSync(filePath, 'utf-8');
        const firstLine = content.split('\n')[0].trim();
        if (firstLine.startsWith('#')) {
            return firstLine.replace(/^#+\s*/, '').trim();
        }
        // Fallback: use filename if no # found
        return path.basename(filePath, '.txt')
            .split('-')
            .map(word => word.charAt(0).toUpperCase() + word.slice(1))
            .join(' ');
    } catch (error) {
        console.warn(` Warning: Could not read title from ${filePath}:`, error.message);
        return path.basename(filePath, '.txt');
    }
}

function readIntroFile(fileName, fallbackText) {
    // Correctly handle script directory in ESM
    const scriptDir = path.dirname(new URL(import.meta.url).pathname);
    const introPath = path.join(scriptDir, fileName);
    try {
        return fs.readFileSync(introPath, 'utf-8').trim();
    } catch (error) {
        return fallbackText;
    }
}

function generateLlmsIndex(docsDir) {
    console.log('Starting llms.txt index generation...');
    const llmsFolder = path.join(docsDir, '_llms');

    if (!fs.existsSync(llmsFolder)) {
        console.log(` Folder does not exist: ${llmsFolder} - skipping`);
        return;
    }

    const files = fs.readdirSync(llmsFolder)
        .filter(file => file.endsWith('.txt'))
        .sort();

    console.log(` Found ${files.length} files in _llms`);

    let content = readIntroFile('llms-intro.txt', '# Kotlin DataFrame documentation\n\nKotlin DataFrame is a typesafe DSL for structured data processing in Kotlin.');
    content += '\n\n';
    
    // Add link to full content
    content += `- [Full Content](${BASE_URL}/llms-full.txt)\n\n`;

    for (const fileName of files) {
        const title = extractTitle(path.join(llmsFolder, fileName));
        const absoluteUrl = `${BASE_URL}/_llms/${fileName}`;
        content += `- [${title}](${absoluteUrl})\n`;
    }

    const outputPath = path.join(docsDir, 'llms.txt');
    const fullPath = path.join(docsDir, 'llms-full.txt');

    // Move existing llms.txt (full content) to llms-full.txt and add intro
    if (fs.existsSync(outputPath) && !fs.existsSync(fullPath)) {
        try {
            const originalContent = fs.readFileSync(outputPath, 'utf-8');
            const fullIntro = readIntroFile('llms-full-intro.txt', '# Kotlin DataFrame Documentation - Full Content\n\nThis file contains the combined content of all documentation topics, optimized for LLMs.');
            fs.writeFileSync(fullPath, fullIntro + '\n\n' + originalContent, 'utf-8');
            console.log(` Created llms-full.txt with intro`);
        } catch (error) {
            console.warn(` Warning: Could not create llms-full.txt from existing llms.txt:`, error.message);
            // If it failed but we still need to proceed, we'll just overwrite llms.txt later
        }
    }

    try {
        fs.writeFileSync(outputPath, content, 'utf-8');
        console.log(` Created: llms.txt (${files.length} files indexed)`);
    } catch (error) {
        console.error(' Error writing llms.txt:', error);
        process.exit(1);
    }
}

const args = process.argv.slice(2);
const docsDir = args[0];

if (!docsDir) {
    console.error('Please provide the documentation directory as an argument.');
    process.exit(1);
}

const startTime = Date.now();
try {
    generateLlmsIndex(path.resolve(docsDir));
    const duration = ((Date.now() - startTime) / 1000).toFixed(2);
    console.log(`\nComplete in ${duration}s`);
} catch (error) {
    console.error('\nError during llms.txt generation:', error);
    process.exit(1);
}
